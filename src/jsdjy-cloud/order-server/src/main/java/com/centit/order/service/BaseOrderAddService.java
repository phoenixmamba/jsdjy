package com.centit.order.service;

import cn.hutool.core.util.RandomUtil;
import com.centit.core.dto.OrderDto;
import com.centit.core.enums.SellTypeEnum;
import com.centit.order.dao.*;
import com.centit.order.enums.OrderAction;
import com.centit.order.enums.OrderStatus;
import com.centit.order.po.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单创建
 * @Date : 2024/12/27 14:47
 **/
@Slf4j
public abstract class BaseOrderAddService {
    @Resource
    private OrderformDao orderformDao;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;
    @Resource
    @Qualifier("CulturalOrderStatusService")
    private OrderStatusService orderStatusService;

    public void addOrder(OrderDto orderDto){
//        //创建临时订单
//        addTempOrder(orderDto);
        //先校验订单信息
        if(validateOrder(orderDto)){
            //创建订单
            createOrder(orderDto);
        }
    }

    /**
     * 创建临时订单
     * @param orderDto 订单信息
     */
    public void addTempOrder(OrderDto orderDto){
        OrderformPo orderformPo = new OrderformPo(orderDto);
        orderformPo.setOrderStatus(OrderStatus.TEMP.getStatus());
        orderformDao.insert(orderformPo);
        orderDto.setId(orderformPo.getId());
    }

    /**
     * 删除临时订单
     * @param id 订单id
     */
    public void delTempOrder(String id){
        orderformDao.deleteOrderById(id);
    }

    /**
     * 校验订单
     * @param orderDto 订单信息
     * @return
     */
    public abstract boolean validateOrder(OrderDto orderDto);

    public void updateOrderWithFail(String ofId,String failMsg){
        //更新临时订单状态，保存订单创建失败原因
        OrderformPo orderformPo = new OrderformPo();
        orderformPo.setId(ofId);
        orderformPo.setOrderStatus(OrderStatus.UN_CREATE.getStatus());
        orderformPo.setMsg(failMsg);
        orderformDao.updateStatus(orderformPo);
    }

    public void createOrder(OrderDto orderDto){
        // 创建一个默认的事务定义对象
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        // 设置事务的名称，便于在日志和调试中识别
        def.setName("CreateOrderAndDeductInventoryTransaction");

        // 设置事务的传播行为，这里表示需要一个现有的事务，如果没有则创建一个新的
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 从事务管理器中获取事务状态，这将开始一个新的事务或者加入到现有的事务中
        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            //保存订单信息
            saveOrderInfo(orderDto);
            //扣减库存
            if(cutStock(orderDto)<0){
                // 扣减库存失败，回滚事务
                log.info("库存扣减失败，创建订单失败，订单信息：{}",orderDto);
                transactionManager.rollback(status);
                updateOrderWithFail(orderDto.getId(),"库存不足");
            }else{
                // 扣减库存成功，提交事务
                transactionManager.commit(status);
                //执行数据库库存扣减
                try{
                    cutDBStock(orderDto);
                }catch (Exception e){
                    log.error("扣减数据库库存失败，订单信息：{}，异常信息：",orderDto,e);
                    //数据库库存扣减失败不回滚，通过定时检查任务或者异步消息进行补偿
                    //ToDo 发送异步消息，处理数据库库存与redis库存不一致
                }
                log.info("订单{}创建成功",orderDto.getOrderId());
            }
        }catch (Exception e){
            // 发生异常，回滚事务
            log.error("创建订单{}异常：",orderDto.getOrderId(),e);
            transactionManager.rollback(status);
        }

    }


    public void saveOrderInfo(OrderDto orderDto){
        saveOrderPaymentInfo(orderDto);
        saveOrderCartInfo(orderDto);
        saveOrderWriteOffInfo(orderDto);
        orderStatusService.setNextStatus(orderDto.getId(), OrderAction.CREATE);
    }

    /**
     * 扣减redis库存
     * @param orderDto
     * @return
     */
    public abstract long cutStock(OrderDto orderDto);

    /**
     * 扣减数据库库存
     * @param orderDto
     */
    public abstract void cutDBStock(OrderDto orderDto);

    private void saveOrderPaymentInfo(OrderDto orderDto) {
        ShoppingOrderPayPo shoppingOrderPay = new ShoppingOrderPayPo();
        shoppingOrderPay.setUserId(orderDto.getUserId());

        if (orderDto.getOrderPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            shoppingOrderPay.setCashStatus(0);
        }
        if (orderDto.getOrderUseIntegralValue() > 0) {
            shoppingOrderPay.setIntegralStatus(0);
        }
        if (orderDto.getOrderDeductionBalancePrice().compareTo(BigDecimal.ZERO) > 0) {
            shoppingOrderPay.setBalanceStatus(0);
        }

        if (StringUtils.isNotBlank(orderDto.getCouponId())) {
            shoppingOrderPay.setCouponStatus(0);
            lockUserCoupon(orderDto);
        }

        if (StringUtils.isNotBlank(orderDto.getAccountPointPayKey()) || StringUtils.isNotBlank(orderDto.getAccountMoneyPayKey())) {
            saveOrderPayKey(orderDto);
        }

        shoppingOrderPay.setOfId(orderDto.getId());
        shoppingOrderPayDao.insert(shoppingOrderPay);
    }

    protected void lockUserCoupon(OrderDto orderDto) {
        ShoppingCouponUsertempPo shoppingCouponUsertemp = new ShoppingCouponUsertempPo();
        shoppingCouponUsertemp.setUserId(orderDto.getUserId());
        shoppingCouponUsertemp.setCouponId(orderDto.getCouponId());
        shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
    }

    protected void saveOrderPayKey(OrderDto orderDto) {
        ShoppingOrderPaykeyPo shoppingOrderPaykey = new ShoppingOrderPaykeyPo();
        shoppingOrderPaykey.setAccountPointPayKey(orderDto.getAccountPointPayKey());
        shoppingOrderPaykey.setAccountMoneyPayKey(orderDto.getAccountMoneyPayKey());
        shoppingOrderPaykey.setOfId(orderDto.getId());
        shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
    }

    protected void saveOrderCartInfo(OrderDto orderDto) {
        ShoppingGoodscartPo goodscart = new ShoppingGoodscartPo();
        if (StringUtils.isNotBlank(orderDto.getCartId())) {
            goodscart.setId(orderDto.getCartId());
            goodscart = shoppingGoodscartDao.queryDetail(goodscart);
            if (StringUtils.isBlank(goodscart.getOfId())) {
                goodscart = new ShoppingGoodscartPo();
            }
        }

        goodscart.setCount(orderDto.getGoodsCount());
        goodscart.setTransport(orderDto.getTransport());
        goodscart.setDeductionCouponPrice(orderDto.getOrderDeductionCouponPrice());
        goodscart.setDeductionMemberPrice(orderDto.getOrderDeductionMemberPrice());
        goodscart.setDeductionIntegral(orderDto.getOrderUseIntegralValue());
        goodscart.setDeductionIntegralPrice(orderDto.getOrderDeductionIntegralPrice());
        goodscart.setDeductionBalancePrice(orderDto.getOrderDeductionBalancePrice());
        goodscart.setPayPrice(orderDto.getOrderPayPrice());
        goodscart.setShipPrice(orderDto.getOrderShipPrice());
        goodscart.setPrice(orderDto.getUnitPrice());
        goodscart.setOfId(orderDto.getId());

        if (StringUtils.isNotBlank(orderDto.getCartId())) {
            shoppingGoodscartDao.update(goodscart);
        } else {
            String scId = getMerchantCartId();
            saveNewCartInfo(goodscart,scId,orderDto);
            orderDto.setCartId(goodscart.getId());
        }
    }

    public abstract void saveNewCartInfo(ShoppingGoodscartPo goodscart,String scId,OrderDto orderDto);

    private String getMerchantCartId() {
        // ToDo 获取商户购物车id
        // 目前统一返回1
        return "1";
    }

    protected void saveOrderWriteOffInfo(OrderDto orderDto) {
        if (orderDto.getTransport().equals("自提") ||
                orderDto.getOrderType() == SellTypeEnum.ACTIVITY.getOrderType() ||
                orderDto.getOrderType() == SellTypeEnum.PLAN.getOrderType()) {
            ShoppingWriteoffPo shoppingWriteOff = new ShoppingWriteoffPo();
            shoppingWriteOff.setGcId(orderDto.getCartId());
            shoppingWriteOff.setGoodsCount(orderDto.getGoodsCount());
            shoppingWriteOff.setOffCode(RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, 6));
            shoppingWriteoffDao.insert(shoppingWriteOff);
        }
    }
}
