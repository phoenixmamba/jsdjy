package com.centit.scan.schedule;

import com.alibaba.fastjson.JSONObject;
import com.centit.scan.utils.HttpSendUtil;
import com.centit.scan.utils.StringUtil;
import com.centit.scan.webmgr.dao.TDingtalkTokenDao;
import com.centit.scan.webmgr.po.TDingtalkToken;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2021/10/19 10:17
 * @description ：
 */
@Component
public class ServerScanTask {

    @Resource
    private TDingtalkTokenDao tDingtalkTokenDao;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduledCronDemo() {
        System.out.println("开始执行微服务扫描任务...");
        //校验142服务
        //校验admin服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10098/webmgr/userinfo/userinfo")){
            sendMsg("172.21.1.142","admin");
        }
        //校验community服务
        if(!HttpSendUtil.doPostForTest("http://172.21.1.142:10092/comm/ArtProductionList?accessToken=ACCESSTOKEN",new JSONObject().toJSONString())){
            sendMsg("172.21.1.142","community");
        }
        //校验djycapability服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10071/dhJcms/queryJCMSInfos?num=1")){
            sendMsg("172.21.1.142","djycapability");
        }
        //校验logstatistics服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10095/reportForm/orderReport")){
            sendMsg("172.21.1.142","logstatistics");
        }
        //校验pay服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10090/appPay/payments")){
            sendMsg("172.21.1.142","pay");
        }
        //校验shopping服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10093/webmgr/sellerGoods/queryGoodsClass")){
            sendMsg("172.21.1.142","shopping");
        }
        //校验ticket服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10099/webmgr/projectManage/ticketClass")){
            sendMsg("172.21.1.142","ticket");
        }
        //校验user服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10087/userCenter/checkMobileIsHaveManyLoginName/13776407246?accessToken=ACCESSTOKEN")){
            sendMsg("172.21.1.142","user");
        }

        //校验143服务
        //校验admin服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10098/webmgr/userinfo/userinfo")){
            sendMsg("172.21.1.143","admin");
        }
        //校验community服务
        if(!HttpSendUtil.doPostForTest("http://172.21.1.143:10092/comm/ArtProductionList?accessToken=ACCESSTOKEN",new JSONObject().toJSONString())){
            sendMsg("172.21.1.143","community");
        }
        //校验djycapability服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10071/dhJcms/queryJCMSInfos?num=1")){
            sendMsg("172.21.1.143","djycapability");
        }
        //校验logstatistics服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10095/reportForm/orderReport")){
            sendMsg("172.21.1.143","logstatistics");
        }
        //校验pay服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10090/appPay/payments")){
            sendMsg("172.21.1.143","pay");
        }
        //校验shopping服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10093/webmgr/sellerGoods/queryGoodsClass")){
            sendMsg("172.21.1.143","shopping");
        }
        //校验ticket服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10099/webmgr/projectManage/ticketClass")){
            sendMsg("172.21.1.143","ticket");
        }
        //校验user服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10087/userCenter/checkMobileIsHaveManyLoginName/13776407246?accessToken=ACCESSTOKEN")){
            sendMsg("172.21.1.143","user");
        }

        //校验file服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.144:10097/common/downloadFile/1")){
            sendMsg("172.21.1.144","file");
        }
        System.out.println("微服务扫描任务执行结束");
    }

    @Scheduled(cron = "0 0 9,17 * * ?")
    public void checkServer() {
        System.out.println("开始执行定点扫描微服务任务...");
        boolean res = true;
        //校验142服务
        //校验admin服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10098/webmgr/userinfo/userinfo")){
            sendMsg("172.21.1.142","admin");
            res = false;
        }
        //校验community服务
        if(!HttpSendUtil.doPostForTest("http://172.21.1.142:10092/comm/ArtProductionList?accessToken=ACCESSTOKEN",new JSONObject().toJSONString())){
            sendMsg("172.21.1.142","community");
            res = false;
        }
        //校验djycapability服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10071/dhJcms/queryJCMSInfos?num=1")){
            sendMsg("172.21.1.142","djycapability");
            res = false;
        }
        //校验logstatistics服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10095/reportForm/orderReport")){
            sendMsg("172.21.1.142","logstatistics");
            res = false;
        }
        //校验pay服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10090/appPay/payments")){
            sendMsg("172.21.1.142","pay");
            res = false;
        }
        //校验shopping服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10093/webmgr/sellerGoods/queryGoodsClass")){
            sendMsg("172.21.1.142","shopping");
            res = false;
        }
        //校验ticket服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10099/webmgr/projectManage/ticketClass")){
            sendMsg("172.21.1.142","ticket");
            res = false;
        }
        //校验user服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.142:10087/userCenter/checkMobileIsHaveManyLoginName/13776407246?accessToken=ACCESSTOKEN")){
            sendMsg("172.21.1.142","user");
            res = false;
        }

        //校验143服务
        //校验admin服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10098/webmgr/userinfo/userinfo")){
            sendMsg("172.21.1.143","admin");
            res = false;
        }
        //校验community服务
        if(!HttpSendUtil.doPostForTest("http://172.21.1.143:10092/comm/ArtProductionList?accessToken=ACCESSTOKEN",new JSONObject().toJSONString())){
            sendMsg("172.21.1.143","community");
            res = false;
        }
        //校验djycapability服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10071/dhJcms/queryJCMSInfos?num=1")){
            sendMsg("172.21.1.143","djycapability");
            res = false;
        }
        //校验logstatistics服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10095/reportForm/orderReport")){
            sendMsg("172.21.1.143","logstatistics");
            res = false;
        }
        //校验pay服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10090/appPay/payments")){
            sendMsg("172.21.1.143","pay");
            res = false;
        }
        //校验shopping服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10093/webmgr/sellerGoods/queryGoodsClass")){
            sendMsg("172.21.1.143","shopping");
            res = false;
        }
        //校验ticket服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10099/webmgr/projectManage/ticketClass")){
            sendMsg("172.21.1.143","ticket");
            res = false;
        }
        //校验user服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.143:10087/userCenter/checkMobileIsHaveManyLoginName/13776407246?accessToken=ACCESSTOKEN")){
            sendMsg("172.21.1.143","user");
            res = false;
        }

        //校验file服务
        if(!HttpSendUtil.doGetForTest("http://172.21.1.144:10097/common/downloadFile/1")){
            sendMsg("172.21.1.144","file");
            res = false;
        }
        System.out.println("定点扫描微服务任务执行结束");
        if(res){
            sendOKMsg();
        }
    }

    public void sendOKMsg(){
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setAgentId(1399512727L);
            request.setUseridList("0460090334762982,015558494720426863");
            request.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("text");
            msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
            msg.getText().setContent("大剧院微服务当前运行正常。"+new Date().getTime());
            request.setMsg(msg);

            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, getToken());
            System.out.println(rsp.getBody());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String ip,String server){
        try {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setAgentId(1399512727L);
        request.setUseridList("0460090334762982,015558494720426863");
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(ip+"服务器的"+server+"服务接口无法正常访问，请立即检查该服务运行状态！"+new Date().getTime());
        request.setMsg(msg);

        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, getToken());
        System.out.println(rsp.getBody());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String  getToken(){
        TDingtalkToken tDingtalkToken = tDingtalkTokenDao.queryDetail(new TDingtalkToken());
        String invaliedTime =tDingtalkToken.getInvaliedTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateInvalid = sdf.parse(invaliedTime);
            Date dateNow = new Date();
            if (!StringUtil.isNotNull(tDingtalkToken.getAccessToken())||dateNow.getTime() >= dateInvalid.getTime()) {  //当前时间大于失效时间
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
                OapiGettokenRequest request = new OapiGettokenRequest();
                request.setAppkey("dinghtyw5czd5xgmwumn");
                request.setAppsecret("mI4ctCXByByAlLhn_z8sjH-UaiUTnEycMoccLNK-zANFkyd70cD2ZRGq9nk0aNsJ");
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);
                System.out.println(response.getBody());
                JSONObject obj = JSONObject.parseObject(response.getBody());
                if(obj.getInteger("errcode")==0&&obj.getString("errmsg").equals("ok")){
                    tDingtalkToken.setAccessToken(obj.getString("access_token"));
                    tDingtalkToken.setInvaliedTime(StringUtil.nowTimePlusMinutes(100));  //设置失效时间为100分钟
                    tDingtalkTokenDao.update(tDingtalkToken);
                    return tDingtalkToken.getAccessToken();
                }else{
                    return null;
                }
            }else{
                return tDingtalkToken.getAccessToken();
            }
        }catch (Exception e) {
            return null;
        }
    }

}