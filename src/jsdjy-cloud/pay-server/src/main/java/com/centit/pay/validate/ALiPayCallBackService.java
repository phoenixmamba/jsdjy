package com.centit.pay.validate;

import com.alipay.api.internal.util.AlipaySignature;
import com.centit.pay.common.contst.Pay;
import com.centit.pay.common.enums.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 支付宝回调处理
 * @Date : 2025/8/28 15:29
 **/
@Service
@Slf4j
public class ALiPayCallBackService extends AbstractPayCallBackService{

    @Value("${payment.alipay}")
    private String paymentAlipay;

    public String handlePayCallBack(HttpServletRequest request, HttpServletResponse response) {
        return super.handlePayCallBack(request, response, paymentAlipay, Const.MZ_PAYID_ALI);
    }

    @Override
    protected Map<String, String> parseRequest(HttpServletRequest request, HttpServletResponse response){
        // 解析支付宝回调数据
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String)iter.next();
            String[] values = (String[])requestParams.get(name);
            String valueStr = "";
            for(int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    @Override
    protected boolean validateSign(Map<String, String> paramMap) {
        try {
            return AlipaySignature.rsaCheckV1(paramMap, Pay.ALIPAY_PUBLIC_KEY, Pay.CHARSET, "RSA2");
        } catch (Exception e) {
            log.error("支付宝签名校验异常", e);
            return false;
        }
    }

    @Override
    protected String getOrderIdFromParams(Map<String, String> paramMap) {
        return paramMap.get("out_trade_no");
    }

    @Override
    protected boolean isPaymentSuccess(Map<String, String> paramMap) {
        String tradeStatus = paramMap.get("trade_status");
        return tradeStatus != null &&
                ("WAIT_SELLER_SEND_GOODS".equals(tradeStatus) ||
                        "TRADE_FINISHED".equals(tradeStatus) ||
                        "TRADE_SUCCESS".equals(tradeStatus));
    }

    @Override
    protected String getPaymentFailMessage(Map<String, String> paramMap) {
//        return "交易状态：" + paramMap.get("trade_status");
        return paramMap.toString();
    }

    @Override
    protected PayCallBackInfo buildPayCallBackInfo(Map<String, String> paramMap, String orderId,
                                                   String payType, String payChannel) {
        BigDecimal totalAmount = new BigDecimal(paramMap.get("total_amount"));
        return new PayCallBackInfo(orderId, payType, payChannel, totalAmount, paramMap.get("trade_no"));
    }

    @Override
    protected String getPayChannelName() {
        return "支付宝";
    }

    @Override
    protected String getParseFailResponse() {
        return "FAILURE";
    }

    @Override
    protected String getSignValidateFailResponse() {
        return "FAILURE";
    }

    @Override
    protected String getOrderNotExistResponse() {
        return "FAILURE";
    }

//    @Override
//    protected String getPaymentFailResponse() {
//        return "FAILURE";
//    }

    @Override
    protected String getSuccessResponse() {
        return "SUCCESS";
    }

}
