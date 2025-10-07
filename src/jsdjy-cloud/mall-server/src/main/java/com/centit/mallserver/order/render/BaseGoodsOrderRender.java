package com.centit.mallserver.order.render;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.BusinessException;
import com.centit.core.model.OrderAmount;
import com.centit.core.result.ResultCodeEnum;
import com.centit.mallserver.dao.GoodsSpecInventoryDao;
import com.centit.mallserver.dao.ShoppingGoodsDao;
import com.centit.mallserver.dao.ShoppingSpecificationDao;
import com.centit.mallserver.dto.ProductDto;
import com.centit.mallserver.po.ShoppingGoodsPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品订单渲染
 * @Date : 2024/12/19 15:35
 **/
@Slf4j
public abstract class BaseGoodsOrderRender extends AbstractMallOrderRender {
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private GoodsSpecInventoryDao goodsSpecInventoryDao;
    @Resource
    private ShoppingSpecificationDao shoppingSpecificationDao;

    protected ShoppingGoodsPo shoppingGoods;
    private JSONObject addObj;


    /**
     * 加载商品信息
     * @param productDto
     */
    @Override
    protected void loadGoodsInfo(ProductDto productDto){
        String goodsId = productDto.getGoodsId();
        //查询商品主体信息
        this.shoppingGoods = shoppingGoodsDao.selectGoodsDetail(goodsId);
    }

    /**
     * 初始化订单金额
     */
    @Override
    protected void initOrderAmount(ProductDto productDto) {
        orderAmount = new OrderAmount();
        // 计算商品价格
        calculateGoodsPrice(productDto);

        // 计算运费
        calculateShippingFee(productDto);

        // 计算总金额
        calculateTotalAmount();
    }

    /**
     * 计算商品价格
     */
    private void calculateGoodsPrice(ProductDto productDto) {
        String propertys = productDto.getPropertys();
        //实时查询商品现价
        BigDecimal currentPrice = StringUtils.isNotBlank(propertys)?goodsSpecInventoryDao.selectInventoryPrice(productDto.getGoodsId(),propertys):shoppingGoods.getStorePrice();
        orderAmount.setCurrentPrice(currentPrice);
    }

    /**
     * 计算运费
     */
    private void calculateShippingFee(ProductDto productDto) {
        //查询收货地址，用于计算运费
        addObj = userInfoService.getUserAddress(productDto.getAddressId(), mzUserId);
        if(shoppingGoods.getGoodsTransfee()== 0 && addObj == null){
            throw new BusinessException(ResultCodeEnum.ORDER_RENDER_FAIL,"未能获取到用户的收货地址");
        }
        //订单运费计算
        BigDecimal shipAmount = countShipPrice(shoppingGoods, productDto.getGoodsCount(), productDto.getTransport(), addObj);
        orderAmount.setShipAmount(shipAmount);
    }

    /**
     * 计算总金额
     */
    private void calculateTotalAmount() {
        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = orderAmount.getCurrentPrice().multiply(new BigDecimal(productDto.getGoodsCount()));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = orderAmount.getShipAmount().add(goodsAmount);
        //订单各金额信息
        orderAmount.setGoodsAmount(goodsAmount);
        orderAmount.setPayAmount(goodsAmount);
        orderAmount.setTotalAmount(totalAmount);
    }

    /**
     * 计算运费
     */
    private BigDecimal countShipPrice(ShoppingGoodsPo shoppingGoods, int goodsCount, String transport, JSONObject addObj) {
        BigDecimal shipPrice = BigDecimal.ZERO;
        if (needShipPrice(shoppingGoods,transport)) {
            //优先通过商品设置的运费模板计算运费，没有指定运费模板时，使用商品直接设置的运费金额
            if (useTransportModel(shoppingGoods)) {
                shipPrice = buildShipPrice(shoppingGoods, goodsCount, addObj);
            } else {
                shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
            }
        }
        return shipPrice;
    }

    /**
     * 是否需要运费
     */
    private boolean needShipPrice(ShoppingGoodsPo shoppingGoods,String transport){
        return shoppingGoods.getGoodsTransfee() == 0 && (StringUtils.isBlank(transport)|| "快递".equals(transport));
    }

    /**
     * 是否使用运费模板
     */
    private boolean useTransportModel(ShoppingGoodsPo shoppingGoods){
        return null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0);
    }

    /**
     * 构建运费
     */
    private BigDecimal buildShipPrice(ShoppingGoodsPo shoppingGoods, int goodsCount, JSONObject addObj) {
        BigDecimal shipPrice = BigDecimal.ZERO;
        //Todo 计算运费

        return shipPrice;
    }

    /***
     * 构建商品订单通用信息
     */
    public void buildGoodsOrderCommonRenderInfo(String orderId){
        //商品信息（单个商品仍按数组形式，与合并下单渲染接口统一）
        JSONObject goodsObj = new JSONObject();
        goodsObj.put("goodsId", shoppingGoods.getId());
        goodsObj.put("goodsName", shoppingGoods.getGoodsName());
        goodsObj.put("currentPrice", goodsOrderRenderVo.getCurrentPrice());
        goodsObj.put("goodsCount", productDto.getGoodsCount());
        goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
        if (StringUtils.isNotBlank(productDto.getPropertys())) {
            goodsObj.put("propertys", productDto.getPropertys());
            List<String> ids = Arrays.asList(productDto.getPropertys().split("_"));
            goodsObj.put("specInfo", shoppingSpecificationDao.selectPropertysValue(ids));
        }
        JSONArray goodsArray = new JSONArray();
        goodsArray.add(goodsObj);
        goodsOrderRenderVo.setGoodsInfoList(goodsArray);
        //配送相关信息
        goodsOrderRenderVo.setAddObj(addObj);
        goodsOrderRenderVo.setGoodsTransfee(shoppingGoods.getGoodsTransfee());
        goodsOrderRenderVo.setSelfextractionSet(shoppingGoods.getSelfextractionSet());
        goodsOrderRenderVo.setSelfextractionAddress(shoppingGoods.getSelfextractionAddress());
        //积分、余额等使用条件
        goodsOrderRenderVo.setUseMembershipSet(shoppingGoods.getUseMembershipSet());
        goodsOrderRenderVo.setUseIntegralSet(shoppingGoods.getUseIntegralSet());
        goodsOrderRenderVo.setUseBalanceSet(shoppingGoods.getUseBalanceSet());
        //会员账户积分与余额
        goodsOrderRenderVo.setAccountPoint(userAccountInfo.getAccountPoint());
        goodsOrderRenderVo.setAccountMoney(userAccountInfo.getAccountMoney());
        //积分和余额免密限额
        goodsOrderRenderVo.setAccountPointLimit(assetRule.getPointAvoidLimit());
        goodsOrderRenderVo.setAccountMoneyLimit(assetRule.getAccountAvoidLimit());
        //积分和余额单次支付的上限配置
        goodsOrderRenderVo.setPointPayLimit(payLimit.getPointPay());
        goodsOrderRenderVo.setBalancePayLimit(payLimit.getBalancePay());
        //订单金额信息
        goodsOrderRenderVo.setCurrentPrice(orderAmount.getCurrentPrice());
        goodsOrderRenderVo.setShipPrice(orderAmount.getShipAmount());
        goodsOrderRenderVo.setPayPrice(orderAmount.getPayAmount());
        goodsOrderRenderVo.setGoodsPrice(orderAmount.getGoodsAmount());
        goodsOrderRenderVo.setTotalPrice(orderAmount.getTotalAmount());
        //抵扣金额信息
        goodsOrderRenderVo.setDeductionCouponPrice(orderAmount.getCouponCut());
        goodsOrderRenderVo.setDeductionMemberPrice(orderAmount.getAccountCut());
        goodsOrderRenderVo.setDeductionIntegralPrice(orderAmount.getIntegralCut());
        goodsOrderRenderVo.setUseIntegralValue(orderAmount.getIntegralValue());
        goodsOrderRenderVo.setDeductionBalancePrice(orderAmount.getBalanceCut());

        //构建商城订单通用信息并计算签名
        goodsOrderRenderVo.setOrderId(orderId);
        goodsOrderRenderVo.setSign(super.buildMallOrderCommonRenderInfo(orderId));
    }
}
