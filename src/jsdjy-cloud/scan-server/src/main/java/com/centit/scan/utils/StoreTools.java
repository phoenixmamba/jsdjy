package com.centit.scan.utils;

import com.centit.scan.webmgr.po.ShoppingStore;
import com.centit.scan.webmgr.po.ShoppingSysconfig;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

@Component
public class StoreTools {

    private static String FileDir="/data/upload/";

    public String createUserFolder(HttpServletRequest request, ShoppingSysconfig config, ShoppingStore store) {
        String path = "";
        String uploadFilePath = config.getUploadFilePath();
        if ("sidImg".equals(config.getImageSaveType())) {
            path =
                    uploadFilePath + File.separator + "store" +
                    File.separator + store.getId();
        }

        if ("sidYearImg".equals(config.getImageSaveType())) {
            path =
                    uploadFilePath + File.separator + "store" +
                    File.separator + store.getId() + File.separator +
                    CommUtil.formatTime("yyyy", new Date());
        }
        if ("sidYearMonthImg".equals(config.getImageSaveType())) {
            path =
                    uploadFilePath + File.separator + "store" +
                    File.separator + store.getId() + File.separator +
                    CommUtil.formatTime("yyyy", new Date()) + File.separator +
                    CommUtil.formatTime("MM", new Date());
        }
        if ("sidYearMonthDayImg".equals(config.getImageSaveType())) {
            path =
                    uploadFilePath + File.separator + "store" +
                    File.separator + store.getId() + File.separator +
                    CommUtil.formatTime("yyyy", new Date()) + File.separator +
                    CommUtil.formatTime("MM", new Date()) + File.separator +
                    CommUtil.formatTime("dd", new Date());
        }
        CommUtil.createFolder(path);
        return path;
    }

    public String createUserFolderURL(ShoppingSysconfig config, ShoppingStore store) {
        String path = "";
        String uploadFilePath = config.getUploadFilePath();
        if ("sidImg".equals(config.getImageSaveType())) {
            path = uploadFilePath + "/store/" + store.getId().toString();
        }

        if ("sidYearImg".equals(config.getImageSaveType())) {
            path = uploadFilePath + "/store/" + store.getId() + "/" +
                    CommUtil.formatTime("yyyy", new Date());
        }
        if ("sidYearMonthImg".equals(config.getImageSaveType())) {
            path = uploadFilePath + "/store/" + store.getId() + "/" +
                    CommUtil.formatTime("yyyy", new Date()) + "/" +
                    CommUtil.formatTime("MM", new Date());
        }
        if ("sidYearMonthDayImg".equals(config.getImageSaveType())) {
            path = uploadFilePath + "/store/" + store.getId() + "/" +
                    CommUtil.formatTime("yyyy", new Date()) + "/" +
                    CommUtil.formatTime("MM", new Date()) + "/" +
                    CommUtil.formatTime("dd", new Date());
        }
        return path;
    }


}




