package com.centit.pay.utils;

import com.centit.pay.biz.po.ShoppingUser;
import com.centit.pay.biz.po.TLmThirdlog;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {

    private static <D> D getCtxBean(String beanName, Class<D> clazz) {
        WebApplicationContext ctx = ContextLoaderListener
                .getCurrentWebApplicationContext();
        return ctx.getBean(beanName, clazz);
    }

    /**
     * 根据userId获取用户
     */
    public static ShoppingUser getShoppingUserByUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        return CommonInit.staticShoppingUserDao.queryDetail(user);
    }

    /**
     * 根据userId获取用户麦座id
     */
    public static String getMzUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user=CommonInit.staticShoppingUserDao.queryDetail(user);
//        if(user==null||user.getMzuserid()==null||"".equals(user.getMzuserid())){
//            return "9253315";
//        }else{
//            return user.getMzuserid();
//        }
        return user.getMzuserid();

    }

    /**
     * 保存第三方请求日志
     *
     */
    public static void addThirdLog(int status,int logType,String logInfo,String reqmethod,String serverpath,String reqtime,
                                   String reqinfo,String rettime,String retinfo){
        TLmThirdlog thirdlog = new TLmThirdlog();
        thirdlog.setLogtype(logType);
        thirdlog.setLoginfo(logInfo);
        thirdlog.setLogip(StringUtil.getLocalIp());
        thirdlog.setReqmethod(reqmethod);
        thirdlog.setServerpath(serverpath);
        thirdlog.setReqtime(reqtime);
        thirdlog.setReqinfo(reqinfo);
        thirdlog.setRettime(rettime);
        thirdlog.setRetinfo(retinfo);
        thirdlog.setStatus(status);
        CommonInit.staticTLmThirdlogDao.insert(thirdlog);
    }
//
//    /**
//     * 获取字典项对应值
//     *
//     * @param catalogCode,dataCode
//     * @return
//     */
//    public static String getCodeValue(String catalogCode,String dataCode) {
//        HashMap<String, Object> reqMap = new HashMap<String, Object>();
//        reqMap.put("catalogCode",catalogCode);
//        reqMap.put("dataCode",dataCode);
//        List<FDatadictionary> list= CommonInit.staticFDatadictionaryDao.queryList(reqMap);
//        if(!list.isEmpty()){
//            return list.get(0).getDataValue();
//        }
//        return "";
//    }

}