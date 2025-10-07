package com.centit.scan.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.scan.webmgr.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>文件服务<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/common")
public class FileController {

    @Resource
    private FileService fileService;

//    @PostMapping("/uploadFile")
//    public void uploadFile(HttpServletRequest request, HttpServletResponse response){
//        System.out.println("123");
//        System.out.println(request);
//    }

    /**
     * 上传文件
     * @return
     */
    @PostMapping("/uploadFile")
    public JSONObject uploadFile(@RequestParam("scan") MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        return fileService.uploadFile(file,request);
    }

    /**
     * 下载文件
     * @return
     */
    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {

        fileService.downloadFile(id,request,response);
    }

}