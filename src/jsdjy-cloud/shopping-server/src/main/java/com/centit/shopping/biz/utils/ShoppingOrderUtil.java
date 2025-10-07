package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.bo.SimpleOrdernfo;
import com.centit.shopping.biz.bo.UserAccountInfo;
import com.centit.shopping.dao.ShoppingGoodsInventoryDao;
import com.centit.shopping.dao.ShoppingTransportDao;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShoppingOrderUtil extends OrderBaseUtil{
    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;
    @Resource
    private ShoppingTransportDao shoppingTransportDao;

    @Resource
    private DBService dbUtil;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_GOODS = "GOODS:";
    @Resource
    private RedisStockService redisStockService;


    /**
     * 获取商品现价
     *
     * @param shoppingGoods
     * @param propertys
     * @return 现价
     */
    public BigDecimal getGoodsCurrentPrice(ShoppingGoods shoppingGoods, String propertys) {
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (StringUtil.isNotNull(propertys)) {
            ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
            shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
            shoppingGoodsInventory.setPropertys(propertys);
            shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
            currentPrice = shoppingGoodsInventory.getPrice();
        } else {
            currentPrice = shoppingGoods.getStorePrice();
        }
        return currentPrice;
    }

    /**
     * 获取文创/积分商品订单可用优惠券列表
     *
     * @param goodsType 商品类型  1：文创；2：积分
     * @param goodsId 商品id
     * @param userId 用户id
     * @param goodsAmount 商品数量
     * @param fixedIntegalValue 定额积分抵扣值
     * @return 优惠券列表
     */
    public JSONArray getGoodsOrderCouponList(int goodsType,String goodsId, String userId, BigDecimal goodsAmount, int fixedIntegalValue) {
        BigDecimal fixedIntegalPrice = new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        JSONArray couponArray = CommonUtil.getGoodsCouppon(goodsId, goodsType, userId, goodsAmount, goodsAmount.subtract(fixedIntegalPrice));
        return couponArray;
    }

    /**
     * 计算运费
     *
     * @param shoppingGoods 商品实体
     * @param goodsCount    购买数量
     * @param transport     收货方式
     * @param addObj     收货地址
     * @param mzUserId      用户麦座id
     * @return BigDecimal 运费
     */
    public BigDecimal countShipPrice(ShoppingGoods shoppingGoods, int goodsCount, String transport, JSONObject addObj, String mzUserId) {

        BigDecimal shipPrice = BigDecimal.ZERO;
        //商品指定为买家承担运费，用户没有选择收货方式或者选择了收货方式为快递时，需要计算快递费
        if (shoppingGoods.getGoodsTransfee() == 0 && (!StringUtil.isNotNull(transport) || transport.equals("快递"))) {
//            JSONObject addObj = null;
//            if (null != addressId) {
//                addObj = MZService.getAddressDetail(mzUserId, addressId);
//            }
            //优先通过商品设置的运费模板计算运费，没有指定运费模板时，使用商品直接设置的运费金额
            if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                shipPrice = buildShipPrice(shoppingGoods, goodsCount, addObj);
            } else {
                shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
            }
        }
        return shipPrice;
    }


    /**
     * 文创商品订单金额计算
     * @param shoppingGoods 商品信息
     * @param shoppingOrderInfo  订单信息
     * @param userAccountInfo  账户信息
     * @return 订单金额信息
     */
    public SimpleOrdernfo countCulOrderAmount(ShoppingGoods shoppingGoods, SimpleOrdernfo shoppingOrderInfo, UserAccountInfo userAccountInfo) {
        int goodsCount = shoppingOrderInfo.getGoodsCount();

        int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
        int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
        int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
        int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = shoppingOrderInfo.getUserId();
        String mzUserId = shoppingOrderInfo.getMzUserid();   //麦座用户id
//        UserAccountInfo userAccountInfo = shoppingOrderInfo.getUserAccountInfo();

        //优先判断收货地址，商品设置为买家承担运费时，用户必须至少要有一个收货地址，否则无法计算运费
        JSONObject addObj = getUserAddress(shoppingOrderInfo.getAddressId(), mzUserId);
        if(shoppingGoods.getGoodsTransfee() == 0 && addObj == null){
            shoppingOrderInfo.setErrorMsg("请先至”设置-收货地址“添加个人收货地址！");
            return shoppingOrderInfo;
        }
        shoppingOrderInfo.setAddObj(addObj);
        //实时查询商品现价
        BigDecimal currentPrice = getGoodsCurrentPrice(shoppingGoods, shoppingOrderInfo.getPropertys());
        //订单运费计算
        BigDecimal shipAmount = countShipPrice(shoppingGoods, goodsCount, shoppingOrderInfo.getTransport(), addObj, mzUserId);
        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = currentPrice.multiply(new BigDecimal(goodsCount));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = shipAmount.add(goodsAmount);
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;
        //定额积分参与折扣类优惠，但不参与抵扣类优惠，因此要提前把定额抵扣积分拿出来，在获取可用优惠券以及计算折扣类优惠时进行处理
        int fixedIntegalValue = useIntegralSet == 1 ? (useIntegralValue * goodsCount) : 0;

        shoppingOrderInfo.setCurrentPrice(currentPrice);
        shoppingOrderInfo.setShipAmount(shipAmount);
        shoppingOrderInfo.setGoodsAmount(goodsAmount);
        shoppingOrderInfo.setTotalAmount(totalAmount);

        /*
                    开始计算根据优先级订单金额，优先级顺序如下：
                    1.优惠券
                    2.会员折扣
                    3.积分抵扣
                    4.余额抵扣*/

        /*
            1.折扣优惠券计算*/
        //从CRM获取用户选择的优惠券详情
        ShoppingCoupon shoppingCoupon = getUserCoupon(shoppingOrderInfo.getCouponId());
        JSONArray couponArray = getGoodsOrderCouponList(1,shoppingGoods.getId(), userId, goodsAmount, fixedIntegalValue);
        shoppingOrderInfo.setCouponArray(couponArray);
        List<String> couponIds = new ArrayList<>();
        for (int i = 0; i < couponArray.size(); i++) {
            JSONObject obj = couponArray.getJSONObject(i);
            couponIds.add(obj.getString("id"));
        }
        //判断用户选择的优惠券是否是有效的优惠券，防止优惠券被篡改
        if(StringUtil.isNotNull(shoppingOrderInfo.getCouponId())&&(shoppingCoupon == null || !couponIds.contains(shoppingOrderInfo.getCouponId()))){
            shoppingOrderInfo.setErrorMsg("当前选择的优惠券信息不存在！");
            return shoppingOrderInfo;
        }
        //折扣优惠
        if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("discount")) {
            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
            //优惠券抵扣的金额：商品金额-(商品金额*折扣值)
            couponCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //这边定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //抵扣优惠
        else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("coincp")) {
            BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()));
            //订单需要支付的现金金额，即商品金额-定额积分抵扣金额（该金额值同样不包含运费，运费在订单费用计算中会单独处理）
            BigDecimal cashAmount = goodsAmount.subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
            //优惠券抵扣的金额：取抵扣金额和现金金额两者中的较小值
            couponCut = couponAmount.compareTo(cashAmount) == 1 ? cashAmount : couponAmount;
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //优惠券抵扣金额
        shoppingOrderInfo.setCouponCut(couponCut);

        /*
            2.会员等级折扣计算
            */
        if (useMembershipSet == 1) {
            //获取会员等级折扣值
            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            accountCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(accountCut);
        }
        shoppingOrderInfo.setAccountCut(accountCut);

        //积分和余额计算，需要先获取卖座的单次支付上限设置
        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
        /*
              3.积分抵扣计算
             */
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
        //如果是定额积分抵扣，需要支付的积分即为经过折扣计算后的fixedIntegalValue
        if (useIntegralSet == 1) {
            integralValue = fixedIntegalValue;
            //定额积分抵扣时，用户账户积分必须足够
            if(integralValue>accountPoint){
                shoppingOrderInfo.setErrorMsg("当前账户积分不足！");
                return shoppingOrderInfo;
            }
            if(integralValue>pointLimitPay){
                shoppingOrderInfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
                return shoppingOrderInfo;
            }
        } else if (useIntegralSet == 2 && shoppingOrderInfo.isUseIntegral()) {  //限额积分抵扣，且用户打开了使用积分的开关
            //商品金额对应的积分值
            int payPriceToInt = payAmount.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
            /*
                     用户最大可抵扣的积分，取商品金额和（useIntegralValue * goodsCount）两者的较小值，
                     因为经过前面的优惠，有可能剩余商品金额已经小于商品设置的积分抵扣最大值
                             */
            int maxIntegralValue = useIntegralValue * goodsCount < payPriceToInt ? useIntegralValue * goodsCount : payPriceToInt;
            //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
            integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        }
        //积分抵扣金额
        integralCut = new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = payAmount.subtract(integralCut);

        shoppingOrderInfo.setIntegralValue(integralValue);
        shoppingOrderInfo.setIntegralCut(integralCut);


        /*
            4.余额抵扣计算
            */
        //运费可以用余额支付，因此在计算余额抵扣时，加上运费
        payAmount = payAmount.add(shipAmount);
        if (useBalanceSet == 1 && shoppingOrderInfo.isUseBalance()) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            payAmount = payAmount.subtract(balanceCut);
        }
        shoppingOrderInfo.setBalanceCut(balanceCut);
        shoppingOrderInfo.setPayAmount(payAmount);

        shoppingOrderInfo.setCountSuccess(true);
        return shoppingOrderInfo;
    }

    /**
     * 积分商品订单金额计算
     * @param shoppingGoods  商品信息
     * @param shoppingOrderInfo  订单信息
     * @param userAccountInfo  账户信息
     * @return  订单金额信息
     */
    public SimpleOrdernfo countIntOrderAmount(ShoppingGoods shoppingGoods, SimpleOrdernfo shoppingOrderInfo, UserAccountInfo userAccountInfo) {
//        ShoppingGoods shoppingGoods = shoppingOrderInfo.getShoppingGoods();
        int goodsCount = shoppingOrderInfo.getGoodsCount();

//        int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
//        int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
        int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
        int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = shoppingOrderInfo.getUserId();
        String mzUserId = shoppingOrderInfo.getMzUserid();   //麦座用户id
//        UserAccountInfo userAccountInfo = shoppingOrderInfo.getUserAccountInfo();

        //优先判断收货地址，商品设置为买家承担运费时，用户必须至少要有一个收货地址，否则无法计算运费
        JSONObject addObj = getUserAddress(shoppingOrderInfo.getAddressId(), mzUserId);
        if(shoppingGoods.getGoodsTransfee() == 0 && addObj == null){
            shoppingOrderInfo.setErrorMsg("请先至”设置-收货地址“添加个人收货地址！");
            return shoppingOrderInfo;
        }
        shoppingOrderInfo.setAddObj(addObj);
        //实时查询商品现价
        BigDecimal currentPrice = getGoodsCurrentPrice(shoppingGoods, shoppingOrderInfo.getPropertys());
        //订单运费计算
        BigDecimal shipAmount = countShipPrice(shoppingGoods, goodsCount, shoppingOrderInfo.getTransport(), addObj, mzUserId);
        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = currentPrice.multiply(new BigDecimal(goodsCount));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = shipAmount.add(goodsAmount);
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;

        shoppingOrderInfo.setCurrentPrice(currentPrice);
        shoppingOrderInfo.setShipAmount(shipAmount);
        shoppingOrderInfo.setGoodsAmount(goodsAmount);
        shoppingOrderInfo.setTotalAmount(totalAmount);




        /*
            会员等级折扣计算
            */
        if (useMembershipSet == 1) {
            //获取会员等级折扣值
            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            accountCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(accountCut);
        }
        shoppingOrderInfo.setAccountCut(accountCut);

        //积分和余额计算，需要先获取卖座的单次支付上限设置
        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();

        /*
              积分抵扣计算
             */
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额

        //用户需要支付的积分额是固定的
        integralValue = payAmount.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
        if(integralValue>accountPoint){
            shoppingOrderInfo.setErrorMsg("当前账户积分不足！");
            return shoppingOrderInfo;
        }
        if(integralValue>pointLimitPay){
            shoppingOrderInfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
            return shoppingOrderInfo;
        }
        //积分抵扣金额
        integralCut = new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = BigDecimal.ZERO;

        shoppingOrderInfo.setIntegralValue(integralValue);
        shoppingOrderInfo.setIntegralCut(integralCut);


        /*
            余额抵扣计算
            */
        //运费可以用余额支付，因此如果存在运费，需要用余额或现金支付
        payAmount = payAmount.add(shipAmount);
        if (useBalanceSet == 1 && shoppingOrderInfo.isUseBalance()) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            payAmount = payAmount.subtract(balanceCut);
        }
        shoppingOrderInfo.setBalanceCut(balanceCut);
        shoppingOrderInfo.setPayAmount(payAmount);

        shoppingOrderInfo.setCountSuccess(true);
        return shoppingOrderInfo;
    }

    /**
     * 扣减商品库存
     * @param goodsId
     * @param propertys
     * @param goodsCount
     * @return  扣减之后剩余的库存【-3:库存扣减失败; -2:库存不足; 大于等于0:扣减库存之后的剩余库存】
     */
    public Long cutGoodsStock(String goodsId,String propertys,int goodsCount){
        //redis扣减商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_GOODS + goodsId+":"+propertys;
            String goods_key = REDIS_KEY_GOODS + goodsId;
            Long stock = redisStockService.cutStockWithPropertys(goods_key,inventory_key,goodsCount);
            if(stock>=0){
               try{
                   //数据库扣减库存
                   dbUtil.cutDBGoodsInventory(goodsId,propertys,goodsCount);
               }catch (Exception e){
                   e.printStackTrace();
                   //数据库扣减库存失败，返库存
                   redisStockService.addStockWithPropertys(goods_key,inventory_key,goodsCount);
                   return -3L;
               }
            }
            return stock;
        }else{
            String goods_key = REDIS_KEY_GOODS + goodsId;
            Long stock = redisStockService.updateStock(goods_key,goodsCount);
            if(stock>=0){
                //扣减数据库库存
                try{
                    dbUtil.cutDBGoodsInventory(goodsId,propertys,goodsCount);
                }catch (Exception e){
                    e.printStackTrace();
                    //数据库扣减库存失败，返库存
                    redisStockService.addStock(goods_key,goodsCount);
                    return -3L;
                }
            }
            return stock;
        }
    }

    /**
     * 增加商品库存
     * @param goodsId
     * @param propertys
     * @param goodsCount
     * @return  增加之后剩余的库存
     */
    public Long addGoodsStock(String goodsId,String propertys,int goodsCount){
        try{
            //数据库增加库存
            dbUtil.addDBGoodsInventory(goodsId,propertys,goodsCount);
        }catch (Exception e){
            e.printStackTrace();
            return -3L;
        }
        //redis增加商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_GOODS + goodsId+":"+propertys;
            String goods_key = REDIS_KEY_GOODS + goodsId;
            return redisStockService.addStockWithPropertys(goods_key,inventory_key,goodsCount);
        }else{
            String goods_key = REDIS_KEY_GOODS + goodsId;
            return redisStockService.addStock(goods_key,goodsCount);
        }
    }

    public BigDecimal buildShipPrice(ShoppingGoods shoppingGoods, int goodsCount, JSONObject addObj) {
        ShoppingTransport shoppingTransport = new ShoppingTransport();
//        //系统默认运费模板
//        shoppingTransport.setId(Const.TRANSPORT_ID);
        //根据商品配置的模板id获取运费模板详情
        shoppingTransport.setId(shoppingGoods.getTransportId());
        shoppingTransport = shoppingTransportDao.queryDetail(shoppingTransport);
        String transInfo = shoppingTransport.getTransEmsInfo() == null ? (shoppingTransport.getTransExpressInfo() == null ? shoppingTransport.getTransMailInfo() : shoppingTransport.getTransExpressInfo()) : shoppingTransport.getTransEmsInfo();

        BigDecimal shipPrice = BigDecimal.ZERO;

        if (shoppingTransport.getTransType() == 3) {  //按地区收费
            if (addObj == null) {
                shipPrice = null;
            } else {
                String cityCode = addObj.getString("city_code");
                String provinceCode = addObj.getString("province_code");
                BigDecimal areaPrice = shoppingTransportDao.queryAreaPrice(cityCode);
                if (areaPrice == null) {
                    areaPrice = shoppingTransportDao.queryAreaPrice(provinceCode);
                }
                shipPrice = areaPrice == null ? new BigDecimal(100) : areaPrice;
            }
        } else if (shoppingTransport.getTransType() == 1) {//按重量计费
            BigDecimal totalWeight = shoppingGoods.getGoodsWeight().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalWeight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalWeight.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首重/首体积
                            shipPrice = transFee;
                        } else {   //超过首重/首体积
                            BigDecimal diffWeight = totalWeight.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        } else if (shoppingTransport.getTransType() == 2) {//按体积计费
            BigDecimal totalVolume = shoppingGoods.getGoodsVolume().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalVolume.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首体积
                            shipPrice = transFee;
                        } else {   //超过首体积
                            BigDecimal diffWeight = totalVolume.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        } else if (shoppingTransport.getTransType() == 0) {//按件计费
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首件
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (goodsCount <= transWeight) {  //未超过首件
                            shipPrice = transFee;
                        } else {   //超过首件
                            BigDecimal diffWeight = new BigDecimal(goodsCount - transWeight);
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        return shipPrice;
    }
}
