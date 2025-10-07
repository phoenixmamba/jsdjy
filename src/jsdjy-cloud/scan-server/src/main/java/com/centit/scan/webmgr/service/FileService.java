package com.centit.scan.webmgr.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface FileService {

    /**
     * 上传文件
     */
    JSONObject uploadFile(MultipartFile file, HttpServletRequest request);

    /**
     * 下载文件
     */
    void downloadFile(String id, HttpServletRequest request, HttpServletResponse response);

}
