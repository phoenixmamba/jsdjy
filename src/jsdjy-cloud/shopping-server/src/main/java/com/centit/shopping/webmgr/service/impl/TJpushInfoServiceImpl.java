package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.TJpushInfoDao;
import com.centit.shopping.po.TJpushInfo;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.TJpushInfoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <p>极光推送历史记录<p>
 *
 * @version : 1.0
 * @Author : lihao
 * @Description : 服务实现类
 * @Date : 2021-01-19
 **/
@Transactional
@Service
public class TJpushInfoServiceImpl implements TJpushInfoService {
    public static final Log log = LogFactory.getLog(TJpushInfoService.class);

    @Resource
    private TJpushInfoDao tJpushInfoDao;


//    @Value("${retCode0}")
//    private String retCode0;
//    @Value("${retMsg0}")
//    private String retMsg0;
//    @Value("${retCode1}")
//    private String retCode1;
//    @Value("${retMsg1}")
//    private String retMsg1;

    /**
     * 新增
     */
    @Override
    public JSONObject create(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "-1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String code = reqJson.getString("code");
            String title = reqJson.getString("title");
            String notification = reqJson.getString("notification");
            String url = reqJson.getString("url");
            String data = reqJson.getString("data");
            JSONArray mobiles = reqJson.getJSONArray("mobiles");

            TJpushInfo tJpushInfo = new TJpushInfo();
            tJpushInfo.setId(StringUtil.UUID());
            tJpushInfo.setCode(code);
            tJpushInfo.setTitle(title);
            tJpushInfo.setNotification(notification);
            tJpushInfo.setUrl(url);
            tJpushInfo.setData(data);

            for (int i = 0; i < mobiles.size(); i++) {
                String mobile = mobiles.getString(i);
                tJpushInfo.setMobile(mobile);
                tJpushInfoDao.insert(tJpushInfo);
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
     * 分页列表查询
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            Integer pageNo = Objects.isNull(reqJson.getInteger("pageNo")) ? 1 : reqJson.getInteger("pageNo");
            Integer pageSize = Objects.isNull(reqJson.getInteger("pageSize")) ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List<TJpushInfo> jpushInfoList = tJpushInfoDao.queryPageList(reqMap);
            bizDataJson.put("jpushInfoList", jpushInfoList);
            bizDataJson.put("total", page.getTotal());

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

}
