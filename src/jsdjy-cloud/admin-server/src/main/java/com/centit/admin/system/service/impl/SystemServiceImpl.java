package com.centit.admin.system.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.config.SystemConfig;
import com.centit.admin.system.po.FOptinfo;
import com.centit.admin.system.po.FUserinfo;
import com.centit.admin.system.po.FUserloginError;
import com.centit.admin.system.po.FUserloginToken;
import com.centit.admin.system.service.SystemService;
import com.centit.admin.system.dao.*;
import com.centit.admin.util.CommonUtil;
import com.centit.admin.util.passwordEncoder.CentitPasswordEncoder;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2020-06-23
 **/
@Transactional
@Service
public class SystemServiceImpl implements SystemService {
    public static final Log log = LogFactory.getLog(SystemService.class);

    @Resource
    private FUserinfoDao fUserinfoDao;

    @Resource
    private FUserunitDao fUserunitDao;

    @Resource
    private FOptinfoDao fOptinfoDao;

    @Resource
    private CentitPasswordEncoder centitPasswordEncoder;

    @Resource
    private FUserloginTokenDao fUserloginTokenDao;

    @Resource
    private FUserloginErrorDao fUserloginErrorDao;

    @Resource
    private SystemConfig systemConfig;

    @Override
    public JSONObject getCode(HttpServletRequest req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String code = RandomUtil.randomString(systemConfig.getVerificationCodeBaseString(),systemConfig.getVerificationCodeLength());
            HttpSession session = req.getSession(true);
            session.setAttribute("randCheckCode", code);
            bizDataJson.put("code",code);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 登录
     */
    @Override
    public Result login(HttpServletRequest request,String loginName, String password) {

        //根据loginName判断用户是否存在
        FUserinfo user = fUserinfoDao.queryUserByLoginName(loginName);
        if(user==null){
            return Result.result(ResultCodeEnum.ADMIN_LOGIN_FAIL.getCode(),"用户不存在");
        }
        //判断用户当前是否限制登录
        if(user.getActiveTime()!=null&&DateUtil.date().before(DateUtil.parse(user.getActiveTime()))){
            return Result.result(ResultCodeEnum.ADMIN_LOGIN_FAIL.getCode(),"当前用户已被禁止登录，将于"+user.getActiveTime()+"后解锁！");
        }
        if(!centitPasswordEncoder.isPasswordValid(user.getUserPin(), password, user.getUserCode())){
            //记录登录错误信息
            FUserloginError fUserloginError = new FUserloginError();
            fUserloginError.setUserCode(user.getUserCode());
            fUserloginErrorDao.insert(fUserloginError);
            //查看连续登录失败次数
            int errorCount = fUserloginErrorDao.selectUserErrorCount(user.getUserCode());
            if(errorCount<systemConfig.getLoginErrorTimes()){
                return Result.result(ResultCodeEnum.ADMIN_LOGIN_FAIL.getCode(),"用户名或密码错误！您还有"+(systemConfig.getLoginErrorTimes()-errorCount)+"次尝试机会！");
            }
            //设置禁止登录时间
            user.setActiveTime(DateUtil.formatTime(DateUtil.offsetMinute(DateUtil.date(),systemConfig.getLoginErrorExpireMinutes())));
            fUserinfoDao.update(user);
            //清除连续登录失败信息
            clearUserLoginError(user.getUserCode());

            return Result.result(ResultCodeEnum.ADMIN_LOGIN_FAIL.getCode(),"用户名或密码错误！您已连续登录失败"+systemConfig.getLoginErrorTimes()+"次，请"+systemConfig.getLoginErrorExpireMinutes()+"分钟后再试！！");
        }

        //清除连续登录失败信息
        clearUserLoginError(user.getUserCode());

        JSONObject userInfo = new JSONObject();
        FUserinfo fUserinfo= fUserinfoDao.queryDetail(user.getUserCode());
        //用户个人信息
        userInfo.put("userInfo",fUserinfo);
        request.getSession().setAttribute(user.getUserCode(),fUserinfo);
        JSONObject bizDataJson = new JSONObject();
        bizDataJson.put("accessToken", userToken(user.getUserCode()));
        bizDataJson.put("userInfo",userInfo);
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * 清除连续登录失败信息
     * @param userCode 用户userCode
     */
    public void clearUserLoginError(String userCode){
        FUserloginError fUserloginError = new FUserloginError();
        fUserloginError.setUserCode(userCode);
        fUserloginErrorDao.delete(fUserloginError);
    }

    /**
     * 获取并更新用户token
     * @param userCode 用户userCode
     * @return String 用户token
     */
    public String userToken(String userCode){
        FUserloginToken fUserloginToken = fUserloginTokenDao.queryDetail(userCode);
        if(fUserloginToken==null||"F".equals(fUserloginToken.getIsValid())){
            //根据时间戳和userCode生成accessToken
            String accessToken = DigestUtil.md5Hex(DateUtil.current()+userCode);
            fUserloginToken = new FUserloginToken();
            fUserloginToken.setUsercode(userCode);
            fUserloginToken.setToken(accessToken);
            fUserloginToken.setIsValid("T");
        }
        fUserloginToken.setExpiretime(DateUtil.formatDateTime(DateUtil.offsetMinute(DateUtil.date(),systemConfig.getLoginExpireTime())));
        fUserloginTokenDao.insertOnDuplicateKey(fUserloginToken);
        return fUserloginToken.getToken();
    }

    /**
     * 修改密码
     */
    @Override
    public JSONObject changepwd(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //先判断旧密码是否正确
            String userCode = reqJson.getString("userCode");
            FUserinfo fUserinfo = fUserinfoDao.queryDetail(userCode);
            String oldpwd = reqJson.getString("oldpwd");
            if(!fUserinfo.getUserPwd().equals(oldpwd)){
                retCode = "102";
                retMsg = "旧密码不正确！";
            }else{
                String newpwd = reqJson.getString("newpwd");
                fUserinfo = new FUserinfo();
                fUserinfo.setUserCode(userCode);
                fUserinfo.setUserPwd(newpwd);
                fUserinfo.setUserPin(centitPasswordEncoder.createPassword(newpwd,userCode));
                fUserinfoDao.update(fUserinfo);
                retCode = "0";
                retMsg = "操作成功！";
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询列表
     */
    @Override
    public JSONObject menu(String userCode, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserinfo fUserinfo = fUserinfoDao.queryDetail(userCode);
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("userCode",fUserinfo.getUserCode());
            reqMap.put("unitCode", CommonUtil.getUserPrimaryUnit(userCode));
            if(null !=reqJson.get("roleType")){
                reqMap.put("roleType",reqJson.getString("roleType"));
                reqMap.put("unitRoleType",reqJson.getString("roleType")+"H");
            }else{
                reqMap.put("roleType","G");
                reqMap.put("unitRoleType","GH");
            }

            List<FOptinfo> opts = fOptinfoDao.queryUserOpt(reqMap);
            List<FOptinfo> res = new ArrayList<>();
            Set<String> sets = new HashSet<String>();
            for(FOptinfo fOptinfo:opts){
                reqMap.clear();
                reqMap.put("optId",fOptinfo.getOptId());
                List<FOptinfo> list=fOptinfoDao.queryUpOpt(reqMap);
                for(FOptinfo fo:list){
                    if(!sets.contains(fo.getOptId())){
                        res.add(fo);
                        sets.add(fo.getOptId());
                    }
                }
            }
            Collections.sort(res);

            JSONArray menu = new JSONArray();
            if(null !=reqJson.get("fid")){
                menuTree(res,menu,reqJson.getString("fid"));
            }else{
                menuTree(res,menu,"0");
            }

            if(!menu.isEmpty()&&null !=menu.getJSONObject(0)){
                JSONArray resArray = menu.getJSONObject(0).getJSONArray("children");
                JSONArray objArray = new JSONArray();
                for(int i=0;i<resArray.size();i++){
                    JSONObject obj = resArray.getJSONObject(i);
                    JSONArray childrenAyyay = obj.getJSONArray("children");
                    if(!childrenAyyay.isEmpty()||!"".equals(obj.get("url"))){
                        objArray.add(obj);
                    }
                }

                bizDataJson.put("objList",objArray);
            }

//            bizDataJson.put("objList",menu);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 验证当前用户是否登录失效
     */
    @Override
    public JSONObject hasLogin(HttpServletRequest req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            Enumeration<String> headers = req.getHeaderNames();
            bizDataJson.put("result",false);
            while (headers.hasMoreElements()) {
                String name = (String) headers.nextElement();
                String value = req.getHeader(name);


                if(name.equalsIgnoreCase("accessToken")){
                    String accessToken = value;
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("token",accessToken);
                    reqMap.put("isValid","T");
                    List<FUserloginToken> list = fUserloginTokenDao.queryList(reqMap);
                    if(list.isEmpty()){
                        bizDataJson.put("result",false);
                    }else{
                        FUserloginToken fUserloginToken = list.get(0);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if(DateUtil.date().before(DateUtil.parse(fUserloginToken.getExpiretime()))){
                            bizDataJson.put("result",false);
                        }else{
                            bizDataJson.put("result",true);
                        }
                    }
                }
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 注销登录
     */
    @Override
    public JSONObject logout(HttpServletRequest req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String name = (String) headers.nextElement();
                String value = req.getHeader(name);

                if(name.equalsIgnoreCase("accessToken")){
                    String accessToken = value;

                    FUserloginToken fUserloginToken = new FUserloginToken();
                    fUserloginToken.setToken(accessToken);
                    fUserloginToken.setIsValid("F");
                    fUserloginTokenDao.logout(fUserloginToken);
                }
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }


    private void menuTree(List<FOptinfo> list, JSONArray array, String fatherId) {

        for (FOptinfo fOptinfo : list) {
            String father = null;
            if (null != fOptinfo.getPreOptId()) {
                father = fOptinfo.getPreOptId();
            }
//            if (father .equals("0") && fatherId .equals("0")) {
            if (father!=null&&father .equals(fatherId)) {
                JSONObject obj = new JSONObject();
                obj.put("id", fOptinfo.getOptId());
                obj.put("isInToolbar", fOptinfo.getIsInToolbar());
                obj.put("optCode", fOptinfo.getOptId());
                obj.put("optId", fOptinfo.getOptId());
                obj.put("icon", fOptinfo.getIcon());
                obj.put("pid", fOptinfo.getPreOptId());
                obj.put("text", fOptinfo.getOptName());
                obj.put("url", fOptinfo.getOptRoute());
                obj.put("attributes", fOptinfo.getAttributes());
//                obj.put("optMethods", fOptinfo.getOptMethods());


                JSONArray children = new JSONArray();
                menuTree(list, children, fOptinfo.getOptId());
                obj.put("children", children);
                array.add(obj);
            }
//            else if (father != null && fatherId != null) {
//                if (father.equals(fatherId)) {
//                    JSONObject obj = new JSONObject();
//                    obj.put("id", fOptinfo.getOptId());
//                    obj.put("isInToolbar", fOptinfo.getIsInToolbar());
//                    obj.put("optCode", fOptinfo.getOptId());
//                    obj.put("optId", fOptinfo.getOptId());
//                    obj.put("icon", fOptinfo.getIcon());
//                    obj.put("pid", fOptinfo.getPreOptId());
//                    obj.put("text", fOptinfo.getOptName());
//                    obj.put("url", fOptinfo.getOptRoute());
//                    obj.put("attributes", fOptinfo.getAttributes());
//                    obj.put("optMethods", fOptinfo.getOptMethods());
//
//                    JSONArray children = new JSONArray();
//                    menuTree(list, children, fOptinfo.getOptId());
//                    obj.put("children", children);
//                    array.add(obj);
//                }
//            }
        }

    }
}
