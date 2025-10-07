package com.centit.shopping.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：文件工具类
 */
public class FileUtil {


    public final static String IMGEXT = "bmp,dib,jfif,gif,jpe,jpeg,jpg,png,tif,tiff,ico";

    /**
     * TODO  上传文件
     *
     * @作者： zhouchaoxi
     * @日期：2018/11/8
     */
    public static Map<String, Object> saveFile(MultipartFile file, HttpServletRequest request, String filePath, String saveFilePathName, String fileName) throws IOException {
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePath);
        Map<String, Object> map = new HashMap<>();
        if ((file != null) && (!file.isEmpty())) {
            String extend = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            String saveFileName = StringUtil.getTimeNo("") + "." + extend;

            fileName = fileName.replace("\\", "/");
//            int t = 1;
//            if (t == 0) {
//                new QiniuUtil().upload(file.getBytes(), fileName + "/" + saveFileName);
//            } else if (t == 1) {
//                PicBucketDemo.upfile(file.getBytes(), saveFileName, fileName);
//            }
            float fileSize = file.getSize();
            File tempFile = new File(saveFilePathName, saveFileName);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            file.transferTo(tempFile);
            BufferedImage img = null;
            if (IMGEXT.contains(extend)) {
                img = ImageIO.read(new File(saveFilePathName + File.separator + saveFileName));
            }
            map.put("width", img == null ? 0 : img.getWidth());
            map.put("height", img == null ? 0 : img.getHeight());
            map.put("mime", extend);
            map.put("fileName", saveFileName);
            map.put("fileSize", Float.valueOf(fileSize));
            map.put("oldName", file.getOriginalFilename());
            map.put("filePath", fileName);
            map.put("allPath", fileName + "/" + saveFileName);
            return map;
        } else {
            map.put("width", 0);
            map.put("height", 0);
            map.put("mime", "");
            map.put("fileName", "");
            map.put("fileSize", 0.0f);
            map.put("oldName", "");
            map.put("filePath", "");
            map.put("allPath", "");
            return map;
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home") );
    }
}