package com.centit.file.shedule;

import com.centit.file.utils.CommonUtil;
import com.centit.file.utils.StoreTools;
import com.centit.file.utils.StringUtil;
import com.centit.file.webmgr.dao.ShoppingAccessoryDao;
import com.centit.file.webmgr.dao.ShoppingSysconfigDao;
import com.centit.file.webmgr.dao.TicketProjectDao;
import com.centit.file.webmgr.dao.TicketProjectImgDao;
import com.centit.file.webmgr.po.ShoppingAccessory;
import com.centit.file.webmgr.po.ShoppingSysconfig;
import com.centit.file.webmgr.po.TicketProject;
import com.centit.file.webmgr.po.TicketProjectImg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/8/25 14:25
 * @description ：关闭超时未支付的订单
 */
@Component
public class DownloadProjectImgTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadProjectImgTask.class);

    @Resource
    private StoreTools storeTools;
    @Resource
    private TicketProjectDao ticketProjectDao;
    @Resource
    private TicketProjectImgDao ticketProjectImgDao;

    @Resource
    private ShoppingSysconfigDao shoppingSysconfigDao;
    @Resource
    private ShoppingAccessoryDao shoppingAccessoryDao;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduledCronDemo() {
        List<TicketProject> projects= ticketProjectDao.queryList(new HashMap<>());
        for(TicketProject tp:projects){
            String projectImgUrl = tp.getProjectImgUrl();
            String imgId = downloadImg(projectImgUrl);
            TicketProjectImg ticketProjectImg = new TicketProjectImg();
            ticketProjectImg.setProjectId(tp.getProjectId());
            ticketProjectImg.setProjectImgUrl(projectImgUrl);
            ticketProjectImg.setImgId(imgId);
            ticketProjectImgDao.insert(ticketProjectImg);
        }


        LOGGER.info("第三方接口健康检查-定时任务结束");
    }

    public String downloadImg(String imgUrl){
        String userId = "1";
        ShoppingSysconfig config=getSysConfig();
//		绝对路径
        String path = storeTools.createUserFolder(null,config, CommonUtil.getSystemStore());
//		相对路径
        String url = storeTools.createUserFolderURL(config,CommonUtil.getSystemStore());

        String extend = "png";
        String saveFileName = StringUtil.getTimeNo("")+StringUtil.randomNumber(4) + "." + extend;
        try {
            URL Url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setRequestMethod("GET");
            int resCode = connection.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int length;
                //读取数据并写入到文件中
                try (FileOutputStream outStream = new FileOutputStream(path+File.separator+saveFileName)) {
                    while ((length = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, length);
                    }
                    outStream.flush();
                } finally {
                    inputStream.close();
                }
            } else {
                System.out.println("文件下载错误码为" + resCode);
            }

            File file = new File(path+File.separator+saveFileName);
            ShoppingAccessory image = new ShoppingAccessory();
            image.setExt(extend);
            image.setPath(url);
//            image.setWidth(StringUtil.null2Int(map.get("width")));
//            image.setHeight(StringUtil.null2Int(map.get("height")));
            image.setName(saveFileName);
            image.setSize(StringUtil.null2Float(file.length()));
            image.setUserId(userId);

            shoppingAccessoryDao.insert(image);

            String imgId = image.getId();

            return imgId;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

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
        return sc;
    }
}