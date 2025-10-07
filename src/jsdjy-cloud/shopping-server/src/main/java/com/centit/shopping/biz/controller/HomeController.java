package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.HomeService;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeService homeService;

    @Resource
    private RedisStockService redisStockService;

    /**
     * 商城推荐
     * @return
     */
    @GetMapping("/homeGoodsList")
    public JSONObject homeGoodsList(HttpServletRequest request){
        return homeService.homeGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 收藏
     * @return
     */
    @PostMapping("/addFavorite")
    public JSONObject addFavorite(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.addFavorite(reqJson,request);
    }

    /**
     * 取消收藏
     * @return
     */
    @PostMapping("/cancelFavorite")
    public JSONObject cancelFavorite(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.cancelFavorite(reqJson,request);
    }

    /**
     * 我的收藏
     * @return
     */
    @GetMapping("/myFavList")
    public JSONObject myFavList(HttpServletRequest request){
        return homeService.myFavList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的浏览轨迹
     * @return
     */
    @GetMapping("/myHistoryList")
    public JSONObject myHistoryList(HttpServletRequest request){
        return homeService.myHistoryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 清除浏览轨迹
     * @return
     */
    @PostMapping("/clearHistory")
    public JSONObject clearHistory(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.clearHistory(reqJson,request);
    }

    /**
     * 全局搜索
     * @return
     */
    @GetMapping("/allSearch")
    public JSONObject allSearch(HttpServletRequest request){
        return homeService.allSearch(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取搜索热词
     * @return
     */
    @GetMapping("/hotSearchWords")
    public JSONObject hotSearchWords(HttpServletRequest request){
        return homeService.hotSearchWords(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 领取优惠券
     * @return
     */
    @PostMapping("/grantCoupon")
    public JSONObject grantCoupon(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.grantCoupon(reqJson,request);
    }

    /**
     * 首页活动弹框
     * @return
     */
    @GetMapping("/homeActivity")
    public JSONObject homeActivity(HttpServletRequest request){
        return homeService.homeActivity(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 一键领取活动优惠券
     * @return
     */
    @PostMapping("/grantActivityCoupon")
    public JSONObject grantActivityCoupon(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.grantActivityCoupon(reqJson,request);
    }

    /**
     * 活动列表
     * @return
     */
    @GetMapping("/activityList")
    public JSONObject activityList(HttpServletRequest request){
        return homeService.activityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的优惠券
     * @return
     */
    @GetMapping("/userCouponList")
    public JSONObject userCouponList(HttpServletRequest request){
        return homeService.userCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的商城优惠券
     * @return
     */
    @GetMapping("/myShoppingCouponList")
    public JSONObject myShoppingCouponList(HttpServletRequest request){
        return homeService.myShoppingCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的停车券
     * @return
     */
    @GetMapping("/myParkCouponList")
    public JSONObject myParkCouponList(HttpServletRequest request){
        return homeService.myParkCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的演出券
     * @return
     */
    @GetMapping("/myTicketCouponList")
    public JSONObject myTicketCouponList(HttpServletRequest request){
        return homeService.myTicketCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的消费券
     * @return
     */
    @GetMapping("/myConsumeCouponList")
    public JSONObject myConsumeCouponList(HttpServletRequest request){
        return homeService.myConsumeCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 领券中心
     * @return
     */
    @GetMapping("/couponCenter")
    public JSONObject couponCenter(HttpServletRequest request){
        return homeService.couponCenter(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 每日签到
     * @return
     */
    @PostMapping("/dailySign")
    public JSONObject dailySign(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.dailySign(reqJson);
    }

    /**
     * 赠送积分
     * @return
     */
    @PostMapping("/addIntegral")
    public JSONObject addIntegral(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.addIntegral(reqJson);
    }

    /**
     * 提交党建预约
     * @return
     */
    @PostMapping("/addAppointment")
    public JSONObject addAppointment(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.addAppointment(reqJson);
    }

    /**
     * 我的党建预约列表
     * @return
     */
    @GetMapping("/myAppointmentList")
    public JSONObject myAppointmentList(HttpServletRequest request){
        return homeService.myAppointmentList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取图文内容
     * @return
     */
    @GetMapping("/getImgtext")
    public JSONObject getImgtext(HttpServletRequest request){
        return homeService.getImgtext(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 兑换优惠码
     * @return
     */
    @PostMapping("/exchangeCouponCode")
    public JSONObject exchangeCouponCode(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return homeService.exchangeCouponCode(reqJson);
    }

    /**
     * 获取我的兑换码记录
     * @return
     */
    @GetMapping("/getMyExchangeCodeRecords")
    public JSONObject getMyExchangeCodeRecords(HttpServletRequest request){
        return homeService.getMyExchangeCodeRecords(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 注销账户
     *
     * @return
     */
    @PostMapping("/userCancellation")
    public JSONObject userCancellation(@RequestBody JSONObject reqJson) {
        return homeService.userCancellation(reqJson);
    }

    /**
     * 测试高并发库存
     * @return
     */
    @GetMapping("/testRedis")
    public Object  testRedis(HttpServletRequest request){
        try {
            return redisStockService.updateStock("testItem", 1);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 测试高并发库存
     * @return
     */
    @GetMapping("/setRedis")
    public void  setRedis(HttpServletRequest request){
        try {
            JSONObject reqJson=RequestParametersUtil.getRequestParametersRetJson(request);
            redisStockService.initStock(reqJson.getString("key"),reqJson.getInteger("count"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}