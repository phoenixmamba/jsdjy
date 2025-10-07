package com.centit.scan.webmgr.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.centit.scan.utils.CommonUtil;
import com.centit.scan.utils.FileUtil;
import com.centit.scan.utils.StoreTools;
import com.centit.scan.utils.StringUtil;
import com.centit.scan.webmgr.dao.ShoppingAccessoryDao;
import com.centit.scan.webmgr.dao.ShoppingSysconfigDao;
import com.centit.scan.webmgr.po.ShoppingAccessory;
import com.centit.scan.webmgr.po.ShoppingSysconfig;
import com.centit.scan.webmgr.service.FileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * <p>系统通用接口<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class FileServiceImpl implements FileService {
    public static final Log log = LogFactory.getLog(FileService.class);

    @Resource
    private StoreTools storeTools;

    @Resource
    private ShoppingSysconfigDao shoppingSysconfigDao;

    @Resource
    private ShoppingAccessoryDao shoppingAccessoryDao;

    public ShoppingSysconfig getSysConfig(HttpServletRequest request) {
        Object obj=request.getSession().getAttribute("SHOP_SYSTEM_CONFIG");
        if (StringUtil.isNotNull(obj)){
            return (ShoppingSysconfig) obj;
        }else{
            ShoppingSysconfig config=getSysConfig();
            request.getSession().setAttribute("SHOP_SYSTEM_CONFIG",config);
            return config;
        }
    }

    /**
     * 上传文件
     */
    @Override
    public JSONObject uploadFile(MultipartFile file, HttpServletRequest request) {

        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String userId = "1";
            ShoppingSysconfig config=getSysConfig(request);
//		绝对路径
            String path = this.storeTools.createUserFolder(request,config, CommonUtil.getSystemStore());
//		相对路径
            String url = this.storeTools.createUserFolderURL(config,CommonUtil.getSystemStore());
            //		返回值
            Map result = new HashMap();
//		图片列表
            List<HashMap<String,Object>> list=new ArrayList<>();
            try {

                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                Map param=multipartRequest.getFileMap();
                Iterator entries =param.entrySet().iterator();
                while (entries.hasNext()) {

                    Map.Entry entry = (Map.Entry) entries.next();
                    String name = (String) entry.getKey();
                    Object valueObj = entry.getValue();
                    if (StringUtil.isNotNull(valueObj)) {
                        System.out.println(name+"<===>"+valueObj);
                        Map map = FileUtil.saveFile(file,request,name, path,url);
                        if (StringUtil.isNotNull(map.get("fileName"))) {
                            ShoppingAccessory image = new ShoppingAccessory();
                            image.setExt((String) map.get("mime"));
                            image.setPath(url);
                            image.setWidth(StringUtil.null2Int(map.get("width")));
                            image.setHeight(StringUtil.null2Int(map.get("height")));
                            image.setName(StringUtil.null2String(map.get("fileName")));
                            image.setSize(StringUtil.null2Float(map.get("fileSize")));
                            image.setUserId(userId);

//                        image.setAlbum(album);
                            shoppingAccessoryDao.insert(image);
//                            String n = config.getImageWebServer() + url + "/" + image.getName();
                            String n = config.getImageWebServer()+ image.getId();
                            HashMap<String, Object> json_map = new HashMap();
                            json_map.put("url", n);
                            json_map.put("id", image.getId());
                            list.add(json_map);
                        }
                    }
                }
                if (list.size()>0){
                    retCode = "0";
                    retMsg = "操作成功！";
                    bizDataJson.put("data",list);
                }else{
                    retCode = "-1";
                    retMsg = "上传失败！";
                }
            } catch (Exception e) {
                e.printStackTrace();
                retCode = "-1";
                retMsg = "系统异常,请稍后重试！";
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
     * 下载文件
     */
    @Override
    public void downloadFile(String id, HttpServletRequest request, HttpServletResponse response) {

        try {
            ShoppingAccessory accessory = new ShoppingAccessory();
            accessory.setId(id);
            accessory = shoppingAccessoryDao.queryDetail(accessory);
            String fileName = accessory.getName();
            if (fileName != null) {
                //设置文件路径
                String realPath = accessory.getPath();
                File file = new File(realPath , fileName);
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
            }
        } catch (Exception e) {
            log.error(e);
        }
    }


    public ShoppingSysconfig getSysConfig() {
        List configs = shoppingSysconfigDao.queryList(new HashMap<>());
        if ((configs != null) && (configs.size() > 0)) {
            ShoppingSysconfig sc = (ShoppingSysconfig) configs.get(0);
            if (sc.getUploadFilePath() == null) {
                sc.setUploadFilePath("upload");
            }
            if (sc.getSysLanguage() == null) {
                sc.setSysLanguage("zh_cn");
            }
            if ((sc.getWebsiteName() == null) || ("".equals(sc.getWebsiteName()))) {
                sc.setWebsiteName("shopping");
            }
            if ((sc.getCloseReason() == null) || ("".equals(sc.getCloseReason()))) {
                sc.setCloseReason("系统维护中...");
            }
            if ((sc.getTitle() == null) || ("".equals(sc.getTitle()))) {
                sc.setTitle("shopping多用户商城系统V2.0版");
            }
            if ((sc.getImageSaveType() == null) ||
                    ("".equals(sc.getImageSaveType()))) {
                sc.setImageSaveType("sidImg");
            }

            if (sc.getImageFilesize() == 0) {
                sc.setImageFilesize(1024);
            }
            if (sc.getSmallWidth() == 0) {
                sc.setSmallWidth(160);
            }
            if (sc.getSmallHeight() == 0) {
                sc.setSmallHeight(160);
            }
            if (sc.getMiddleWidth() == 0) {
                sc.setMiddleWidth(300);
            }
            if (sc.getMiddleHeight() == 0) {
                sc.setMiddleHeight(300);
            }
            if (sc.getBigHeight() == 0) {
                sc.setBigHeight(1024);
            }
            if (sc.getBigWidth() == 0) {
                sc.setBigWidth(1024);
            }
            if ((sc.getImageSuffix() == null) || ("".equals(sc.getImageSuffix()))) {
                sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
            }

//            if (sc.getStoreImage() == null) {
//                Accessory storeImage = new Accessory();
//                storeImage.setPath("resources/style/common/images");
//                storeImage.setName("store.jpg");
//                sc.setStoreImage(storeImage);
//            }
//            if (sc.getGoodsImage() == null) {
//                Accessory goodsImage = new Accessory();
//                goodsImage.setPath("resources/style/common/images");
//                goodsImage.setName("good.jpg");
//                sc.setGoodsImage(goodsImage);
//            }
//            if (sc.getMemberIcon() == null) {
//                Accessory memberIcon = new Accessory();
//                memberIcon.setPath("resources/style/common/images");
//                memberIcon.setName("member.jpg");
//                sc.setMemberIcon(memberIcon);
//            }
            if ((sc.getSecurityCodeType() == null) ||
                    ("".equals(sc.getSecurityCodeType()))) {
                sc.setSecurityCodeType("normal");
            }
            if ((sc.getWebsiteCss() == null) || ("".equals(sc.getWebsiteCss()))) {
                sc.setWebsiteCss("default");
            }
            return sc;
        }
        ShoppingSysconfig sc = new ShoppingSysconfig();
        sc.setUploadFilePath("upload");
        sc.setWebsiteName("shopping");
        sc.setSysLanguage("zh_cn");
        sc.setTitle("shopping多用户商城系统V2.0版");
        sc.setSecurityCodeType("normal");
        sc.setEmailEnable(true);
        sc.setCloseReason("系统维护中...");
        sc.setImageSaveType("sidImg");
        sc.setImageFilesize(1024);
        sc.setSmallWidth(160);
        sc.setSmallHeight(160);
        sc.setMiddleHeight(300);
        sc.setMiddleWidth(300);
        sc.setBigHeight(1024);
//     sc.setFenxiao_type(2);
        sc.setBigWidth(1024);
        sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
//        sc.setComplaint_time(30);
        sc.setWebsiteCss("default");

//        Accessory goodsImage = new Accessory();
//        goodsImage.setPath("resources/style/common/images");
//        goodsImage.setName("good.jpg");
//        sc.setGoodsImage(goodsImage);
//        Accessory storeImage = new Accessory();
//        storeImage.setPath("resources/style/common/images");
//        storeImage.setName("store.jpg");
//        sc.setStoreImage(storeImage);
//        Accessory memberIcon = new Accessory();
//        memberIcon.setPath("resources/style/common/images");
//        memberIcon.setName("member.jpg");
//        sc.setMemberIcon(memberIcon);
        return sc;
    }


}
