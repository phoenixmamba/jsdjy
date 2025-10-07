package com.centit.mallserver.order.validate;

import com.centit.core.dto.OrderDto;
import com.centit.mallserver.order.render.vo.GoodsOrderRenderVo;
import lombok.Data;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单金额校验参数类
 * @Date : 2025/8/27 9:17
 **/

@Data
public class OrderValidatorParams {
    private String orderId;
    private BigDecimal goodsPrice;
    private BigDecimal shipPrice;
    private BigDecimal totalPrice;
    private BigDecimal payPrice;
    private BigDecimal deductionCouponPrice;
    private BigDecimal deductionMemberPrice;
    private BigDecimal deductionIntegralPrice;
    private BigDecimal deductionBalancePrice;

    public OrderValidatorParams() {
        // 初始化默认值
        this.goodsPrice = BigDecimal.ZERO.setScale(2);
        this.shipPrice = BigDecimal.ZERO.setScale(2);
        this.totalPrice = BigDecimal.ZERO.setScale(2);
        this.payPrice = BigDecimal.ZERO.setScale(2);
        this.deductionCouponPrice = BigDecimal.ZERO.setScale(2);
        this.deductionMemberPrice = BigDecimal.ZERO.setScale(2);
        this.deductionIntegralPrice = BigDecimal.ZERO.setScale(2);
        this.deductionBalancePrice = BigDecimal.ZERO.setScale(2);
    }

    // 从 GoodsOrderRenderVo 构造
    public OrderValidatorParams(GoodsOrderRenderVo goodsOrderRenderVo) {
        this(); // 调用无参构造函数初始化默认值
        // 手动映射各个字段
        this.orderId = goodsOrderRenderVo.getOrderId();
        this.goodsPrice = convertToBigDecimal(goodsOrderRenderVo.getGoodsPrice());
        this.shipPrice = convertToBigDecimal(goodsOrderRenderVo.getShipPrice());
        this.totalPrice = convertToBigDecimal(goodsOrderRenderVo.getTotalPrice());
        this.payPrice = convertToBigDecimal(goodsOrderRenderVo.getPayPrice());
        this.deductionCouponPrice = convertToBigDecimal(goodsOrderRenderVo.getDeductionCouponPrice());
        this.deductionMemberPrice = convertToBigDecimal(goodsOrderRenderVo.getDeductionMemberPrice());
        this.deductionIntegralPrice = convertToBigDecimal(goodsOrderRenderVo.getDeductionIntegralPrice());
        this.deductionBalancePrice = convertToBigDecimal(goodsOrderRenderVo.getDeductionBalancePrice());
    }

    // 从 OrderDto 构造
    public OrderValidatorParams(OrderDto orderDto) {
        this();
        this.orderId = orderDto.getOrderId();
        this.goodsPrice = convertToBigDecimal(orderDto.getUnitPrice());
        this.shipPrice = convertToBigDecimal(orderDto.getOrderShipPrice());
        this.totalPrice = convertToBigDecimal(orderDto.getOrderTotalPrice());
        this.payPrice = convertToBigDecimal(orderDto.getOrderPayPrice());
        this.deductionCouponPrice = convertToBigDecimal(orderDto.getOrderDeductionCouponPrice());
        this.deductionMemberPrice = convertToBigDecimal(orderDto.getOrderDeductionMemberPrice());
        this.deductionIntegralPrice = convertToBigDecimal(orderDto.getOrderDeductionIntegralPrice());
        this.deductionBalancePrice = convertToBigDecimal(orderDto.getOrderDeductionBalancePrice());
    }

    /**
     * 将任意类型转换为保留两位小数的 BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }

        try {
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (value instanceof Number) {
                return new BigDecimal(value.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else if (value instanceof String) {
                if (((String) value).isEmpty()) {
                    return BigDecimal.ZERO.setScale(2);
                }
                return new BigDecimal((String) value).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                return BigDecimal.ZERO.setScale(2);
            }
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO.setScale(2);
        }
    }

    /**
     * 生成签名 - 按字段名ASCII码排序后拼接并加密
     * @param secretKey 密钥
     * @return 签名值
     */
    public String generateSignature(String secretKey) {
        try {
            // 获取所有字段并按字段名ASCII排序
            Map<String, Object> fieldMap = new TreeMap<>();
            Field[] fields = this.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(this);

                // 只处理非null且非空的字段
                if (fieldValue != null &&
                        !(fieldValue instanceof String && ((String) fieldValue).isEmpty())) {
                    fieldMap.put(fieldName, fieldValue);
                }
            }

            // 拼接字段名和字段值
            StringBuilder toSign = new StringBuilder();
            for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                if (toSign.length() > 0) {
                    toSign.append("&");
                }
                toSign.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue().toString());
            }

            // 使用HMAC-SHA256进行签名
            return hmacSha256(toSign.toString(), secretKey);
        } catch (Exception e) {
            throw new RuntimeException("签名生成失败", e);
        }
    }

    /**
     * HMAC-SHA256加密
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密后的签名值（十六进制字符串）
     */
    private String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256加密失败", e);
        }
    }

}
