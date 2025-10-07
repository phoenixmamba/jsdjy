package com.centit.pay.validate;

import com.centit.pay.utils.PayUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>移动支付<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-10
 **/
@RestController
@RequestMapping("/notify")
public class PayCallBackController {

    @Resource
    private WxPayCallBackService wxPayCallBackService;

    @Resource
    private ALiPayCallBackService aLiPayCallBackService;

    /**
     * 微信支付回调
     */
    @RequestMapping("/wxNotify")
    public void wxNotify( HttpServletRequest request, HttpServletResponse response){
        String resXml = wxPayCallBackService.handlePayCallBack(request,response);
        PayUtil.sendToCFT(resXml, response);
    }

    /**
     * 支付宝支付回调
     */
    @RequestMapping("/aliNotify")
    public void aliNotify( HttpServletRequest request, HttpServletResponse response){
        String res= aLiPayCallBackService.handlePayCallBack(request,response);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(res);
        out.flush();
    }
}