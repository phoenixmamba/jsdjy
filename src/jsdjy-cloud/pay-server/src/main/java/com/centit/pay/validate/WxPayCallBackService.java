package com.centit.pay.validate;

import com.centit.pay.biz.po.ShoppingPayment;
import com.centit.pay.common.enums.Const;
import com.centit.pay.utils.CommUtil;
import com.centit.pay.utils.PayUtil;
import com.centit.pay.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/8/28 15:29
 **/
@Service
@Slf4j
public class WxPayCallBackService extends AbstractPayCallBackService{
//    @Resource
//    private PayHandleService payHandleService;
//    @Resource
//    private ShoppingOrderformDao shoppingOrderformDao;

    @Value("${payment.wxpay}")
    private String paymentWxpay;

    private static final String PARSE_FAIL_XML = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文解析失败]]></return_msg>" + "</xml> ";

    private static final String SIGN_VALIDATE_XML = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[签名校验失败]]></return_msg>" + "</xml> ";

    private static final String ORDER_NOT_EXIST_XML = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[订单信息不存在]]></return_msg>" + "</xml> ";

    private static final String SUCCESS_XML = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

    private static final String PAYMENT_FAIL_XML = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[支付失败]]></return_msg>" + "</xml> ";

    public String handlePayCallBack(HttpServletRequest request, HttpServletResponse response) {
        return super.handlePayCallBack(request, response, paymentWxpay, Const.MZ_PAYID_WX);
    }

    @Override
    protected Map<String, String> parseRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 解析微信回调数据
        ServletInputStream in = request.getInputStream();
        int size = request.getContentLength();
        byte[] bdata = new byte[size];
        in.read(bdata);
        String xmlString = new String(bdata, StringUtil.getCharacterEncoding(request, response));
        return PayUtil.doXMLParse(xmlString);
    }

    @Override
    protected boolean validateSign(Map<String, String> paramMap) {
        try {
            ShoppingPayment shoppingPayment = payHandleService.getPayment(paymentWxpay);
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            for (String key : paramMap.keySet()) {
                packageParams.put(key, paramMap.get(key));
            }
            String sign = PayUtil.getSign("UTF-8", packageParams, shoppingPayment.getWeixinPartnerkey());
            return !CommUtil.isNull(paramMap.get("sign")) && sign.equals(paramMap.get("sign"));
        } catch (Exception e) {
            log.error("微信签名校验异常", e);
            return false;
        }
    }

    @Override
    protected String getOrderIdFromParams(Map<String, String> paramMap) {
        return paramMap.get("out_trade_no");
    }

    @Override
    protected boolean isPaymentSuccess(Map<String, String> paramMap) {
        return "SUCCESS".equalsIgnoreCase(paramMap.get("return_code")) &&
                "SUCCESS".equalsIgnoreCase(paramMap.get("result_code"));
    }

    @Override
    protected String getPaymentFailMessage(Map<String, String> paramMap) {
        return paramMap.get("return_msg");
    }

    @Override
    protected PayCallBackInfo buildPayCallBackInfo(Map<String, String> paramMap, String orderId,
                                                   String payType, String payChannel) {
        int totalFee = Integer.parseInt(paramMap.get("total_fee"));
        BigDecimal amount = BigDecimal.valueOf(totalFee)
                .divide(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        return new PayCallBackInfo(orderId, payType, payChannel, amount, paramMap.get("transaction_id"));
    }

    @Override
    protected String getPayChannelName() {
        return "微信";
    }

    @Override
    protected String getParseFailResponse() {
        return PARSE_FAIL_XML;
    }

    @Override
    protected String getSignValidateFailResponse() {
        return SIGN_VALIDATE_XML;
    }

    @Override
    protected String getOrderNotExistResponse() {
        return ORDER_NOT_EXIST_XML;
    }

//    @Override
//    protected String getPaymentFailResponse() {
//        return PAYMENT_FAIL_XML;
//    }

    @Override
    protected String getSuccessResponse() {
        return SUCCESS_XML;
    }


}
