package com.centit.shopping.webmgr.service.impl;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.centit.shopping.po.ShoppingSysconfig;
import com.centit.shopping.po.TExportFile;
import com.centit.shopping.dao.TExportFileDao;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.webmgr.service.TExportFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2022-07-24
 **/
@Transactional
@Service
public class TExportFileServiceImpl implements TExportFileService {
    public static final Log log = LogFactory.getLog(TExportFileService.class);

    @Resource
    private TExportFileDao tExportFileDao;


    /**
     * 查询列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);

            bizDataJson.put("total", tExportFileDao.queryTotalCount(reqMap));
            List<TExportFile> objList = tExportFileDao.queryList(reqMap);
            bizDataJson.put("objList", objList);


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
     * 导出文件
     */
    @Override
    public void exportFile(JSONObject reqJson, HttpServletResponse response) {
        try {

            String fileId = reqJson.getString("fileId");
            TExportFile tExportFile = new TExportFile();
            tExportFile.setId(fileId);
            tExportFile = tExportFileDao.queryDetail(tExportFile);
            String fileName = tExportFile.getFileName();
            ShoppingSysconfig config= CommonUtil.getSysConfig();
            String uploadFilePath = config.getUploadFilePath();
            File file = new File(uploadFilePath + File.separator + "exportFile" +
                    File.separator +fileName);
            if (file.exists()) {
                response.setContentType("application/octet-stream");//
                response.setHeader("Content-Length", ""+file.length());
                response.setHeader("content-type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error(e);
        }
    }
}
