package com.centit.logstatistics.logstatisticsserver.webmgr.utils;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.FUserinfo;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingSysconfig;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingUser;

import java.util.HashMap;
import java.util.List;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {


    /**
     * 根据userId获取用户
     */
    public static ShoppingUser getShoppingUserByUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        return CommonInit.staticShoppingUserDao.queryDetail(user);
    }

    public static FUserinfo getFUserInfo(String userId){
        FUserinfo user = new FUserinfo();
        user.setUserCode(userId);
        user=CommonInit.staticFUserinfoDao.queryDetail(user);
        return user;

    }

    public static ShoppingSysconfig getSysConfig() {
        List configs = CommonInit.staticShoppingSysconfigDao.queryList(new HashMap<>());
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