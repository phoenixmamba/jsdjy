package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.WebCommonService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/common")
public class WebCommonController {

    @Resource
    private WebCommonService webCommonService;


    /**
     * 查询会员权益设置
     * @return
     */
    @GetMapping("/memberShipDetail")
    public JSONObject webCommonService(HttpServletRequest request){
        return webCommonService.queryMemberShipDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 保存会员权益设置
     * @return
     */
    @PostMapping("/saveMemnerShip")
    public JSONObject saveMemnerShip(@RequestBody JSONObject reqJson){
        return webCommonService.saveMemnerShip(reqJson);
    }

    /**
     * 商城商品推送
     * @return
     */
    @PostMapping("/pushGoodsMsg")
    public JSONObject pushGoodsMsg(@RequestBody JSONObject reqJson){
        return webCommonService.pushGoodsMsg(reqJson);
    }

    /**
     * 全员推送
     * @return
     */
    @PostMapping("/pushAllMsg")
    public JSONObject pushAllMsg(@RequestBody JSONObject reqJson){
        return webCommonService.pushAllMsg(reqJson);
    }

    /**
     * 获取具有核销权限的账户列表
     * @return
     */
    @GetMapping("/allWriteOffCounts")
    public JSONObject allWriteOffCounts(HttpServletRequest request){
        return webCommonService.allWriteOffCounts(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询积分赠送设置设置
     * @return
     */
    @GetMapping("/integralSetDetail")
    public JSONObject queryIntegralSetDetail(HttpServletRequest request){
        return webCommonService.queryIntegralSetDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 保存积分赠送每日总上限
     * @return
     */
    @PostMapping("/saveIntegralDailyTotal")
    public JSONObject saveIntegralDailyTotal(@RequestBody JSONObject reqJson){
        return webCommonService.saveIntegralDailyTotal(reqJson);
    }

    /**
     * 判断积分赠送项名称是否可用
     * @return
     */
    @GetMapping("/checkIntegralSetName")
    public JSONObject checkIntegralSetName(HttpServletRequest request){
        return webCommonService.checkIntegralSetName(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增积分赠送设置
     * @return
     */
    @PostMapping("/addIntegralSet")
    public JSONObject addIntegralSet(@RequestBody JSONObject reqJson){
        return webCommonService.addIntegralSet(reqJson);
    }

    /**
     * 修改积分赠送配置项
     * @return
     */
    @PostMapping("/editIntegralSet")
    public JSONObject editIntegralSet(@RequestBody JSONObject reqJson){
        return webCommonService.editIntegralSet(reqJson);
    }

    /**
     * 删除积分赠送配置项
     * @return
     */
    @PostMapping("/delIntegralSet")
    public JSONObject delIntegralSet(@RequestBody JSONObject reqJson){
        return webCommonService.delIntegralSet(reqJson);
    }

    /**
     * 党建预约列表
     * @return
     */
    @GetMapping("/appointmentList")
    public JSONObject appointmentList(HttpServletRequest request){
        return webCommonService.appointmentList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/testInt")
    public JSONObject testInt(HttpServletRequest request){
        return webCommonService.testInt(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 图文内容列表
     * @return
     */
    @GetMapping("/imgtextList")
    public JSONObject imgtextList(HttpServletRequest request){
        return webCommonService.imgtextList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取图文内容可选类型
     * @return
     */
    @GetMapping("/imgtextTypes")
    public JSONObject imgtextTypes(HttpServletRequest request){
        return webCommonService.imgtextTypes(RequestParametersUtil.getRequestParametersRetJson(request));
    }

//    /**
//     * 判断编码是否可用
//     * @return
//     */
//    @GetMapping("/checkImgtextCode")
//    public JSONObject checkImgtextCode(HttpServletRequest request){
//        return webCommonService.checkImgtextCode(RequestParametersUtil.getRequestParametersRetJson(request));
//    }

    /**
     * 新增图文内容
     * @return
     */
    @PostMapping("/saveImgtext")
    public JSONObject saveImgtext(@RequestBody JSONObject reqJson){
        return webCommonService.saveImgtext(reqJson);
    }

    /**
     * 编辑图文内容
     * @return
     */
    @PostMapping("/updateImgtext")
    public JSONObject updateImgtext(@RequestBody JSONObject reqJson){
        return webCommonService.updateImgtext(reqJson);
    }

    /**
     * 删除图文内容
     * @return
     */
    @PostMapping("/delImgtext")
    public JSONObject delImgtext(@RequestBody JSONObject reqJson){
        return webCommonService.delImgtext(reqJson);
    }

    /**
     * 充值活动列表
     * @return
     */
    @GetMapping("/rechargeActivityList")
    public JSONObject rechargeActivityList(HttpServletRequest request){
        return webCommonService.rechargeActivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增充值活动
     * @return
     */
    @PostMapping("/addRechargeActivity")
    public JSONObject addRechargeActivity(@RequestBody JSONObject reqJson){
        return webCommonService.addRechargeActivity(reqJson);
    }

    /**
     * 编辑充值活动
     * @return
     */
    @PostMapping("/editRechargeActivity")
    public JSONObject editRechargeActivity(@RequestBody JSONObject reqJson){
        return webCommonService.editRechargeActivity(reqJson);
    }

    /**
     * 上/下架充值活动
     * @return
     */
    @PostMapping("/pubRechargeActivity")
    public JSONObject pubRechargeActivity(@RequestBody JSONObject reqJson){
        return webCommonService.pubRechargeActivity(reqJson);
    }

    /**
     * 删除充值活动
     * @return
     */
    @PostMapping("/delRechargeActivity")
    public JSONObject delRechargeActivity(@RequestBody JSONObject reqJson){
        return webCommonService.delRechargeActivity(reqJson);
    }

    /**
     * 充值活动已参加用户列表
     * @return
     */
    @GetMapping("/rechargeActivityUserList")
    public JSONObject rechargeActivityUserList(HttpServletRequest request){
        return webCommonService.rechargeActivityUserList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取支付限额配置
     * @return
     */
    @GetMapping("/getPayLimit")
    public JSONObject getPayLimit(HttpServletRequest request){
        return webCommonService.getPayLimit(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑支付限额
     * @return
     */
    @PostMapping("/editPayLimit")
    public JSONObject editPayLimit(@RequestBody JSONObject reqJson){
        return webCommonService.editPayLimit(reqJson);
    }

    /**
     * 获取会员资产限额规则
     * @return
     */
    @GetMapping("/getAssetRule")
    public JSONObject getAssetRule(HttpServletRequest request){
        return webCommonService.getAssetRule(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 同步会员资产限额规则
     * @return
     */
    @PostMapping("/syncAssetRule")
    public JSONObject syncAssetRule(@RequestBody JSONObject reqJson){
        return webCommonService.syncAssetRule(reqJson);
    }

    /**
     * 绑定麦座优惠码
     * @return
     */
    @PostMapping("/bindMZCoupon")
    public JSONObject bindMZCoupon(@RequestBody JSONObject reqJson){
        return webCommonService.bindMZCoupon(reqJson);
    }

    /**
     * 查询未绑定指定优惠码的用户列表
     * @return
     */
    @GetMapping("/couponUnBindUserList")
    public JSONObject couponUnBindUserList(HttpServletRequest request){
        return webCommonService.couponUnBindUserList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 绑定麦座优惠码（按注册时间）
     * @return
     */
    @PostMapping("/bindMZCouponByAddTime")
    public JSONObject bindMZCouponByAddTime(@RequestBody JSONObject reqJson){
        return webCommonService.bindMZCouponByAddTime(reqJson);
    }
    /**
     * 优惠码绑定记录
     * @return
     */
    @GetMapping("/couponBindList")
    public JSONObject couponBindList(HttpServletRequest request){
        return webCommonService.couponBindList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出注册用户
     * @return
     */
    @GetMapping("/exportRegUserList")
    public JSONObject exportRegUserList(HttpServletRequest request, HttpServletResponse response){
        return webCommonService.exportRegUserList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }
}