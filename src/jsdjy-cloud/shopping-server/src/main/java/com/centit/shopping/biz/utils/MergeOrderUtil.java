package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.bo.MultOrdernfo;
import com.centit.shopping.biz.bo.UserAccountInfo;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class MergeOrderUtil extends OrderBaseUtil{
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;
    @Resource
    private ShoppingTransportDao shoppingTransportDao;
    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;
    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;
    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingOrderUtil shoppingOrderUtil;
    @Resource
    private ArtOrderUtil artOrderUtil;
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
    public static final String REDIS_KEY_ACT = "ACT:";
    public static final String REDIS_KEY_PLAN ="PLAN:";
    @Resource
    private RedisStockService redisStockService;

    /**
     * 整理合并支付订单的商品信息
     * @param goodsList 商品列表
     * @param multOrdernfo  合并支付订单
     */
    public void buildGoodsArray(JSONArray goodsList,MultOrdernfo multOrdernfo){
        JSONArray goodsArray = new JSONArray();
        BigDecimal goodsPrice = BigDecimal.ZERO;
        BigDecimal shipPrice = BigDecimal.ZERO;
        List<Map<String, Object>> goodsTransList = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goodsOne = goodsList.getJSONObject(i);
            //商品id
            String goodsId = goodsOne.getString("goodsId");
            //购买的商品数量
            int goodsCount = goodsOne.getInteger("goodsCount");
            //商品类型
            int cartType = goodsOne.getInteger("cartType");
            //商品规格信息
            String propertys = goodsOne.get("propertys")==null?null:goodsOne.getString("propertys");
            //规格属性信息
            String specInfo = propertys==null?"":getGoodsSpecInfo(propertys);
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("goodsId", goodsId);
            goodsObj.put("cartType", cartType);
            goodsObj.put("goodsCount", goodsCount);
            //提交订单时还会传递下面的属性
            //购物车id
            String cartId = goodsOne.getString("cartId");
            //移动端传递的商品单价
            BigDecimal unitPrice = goodsOne.getBigDecimal("unitPrice");
            //报名信息
            String signupInfos = goodsOne.getString("signupInfos");
            goodsObj.put("cartId", cartId);
            goodsObj.put("signupInfos", signupInfos);
            //文创商品或积分商品
            if (cartType == Const.SHOPPING_CUL_CART_TYPE || cartType == Const.SHOPPING_INT_CART_TYPE) {
                ShoppingGoods shoppingGoods = new ShoppingGoods();
                shoppingGoods.setId(goodsId);
                //查询商品主体信息
                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                //实时查询商品现价
                BigDecimal currentPrice = shoppingOrderUtil.getGoodsCurrentPrice(shoppingGoods, propertys);
                goodsObj.put("goodsName", shoppingGoods.getGoodsName());
                goodsObj.put("currentPrice", currentPrice);
                goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
                goodsObj.put("limitBuy", shoppingGoods.getLimitBuy());
                if (StringUtil.isNotNull(propertys)) {
                    goodsObj.put("propertys", propertys);
                    goodsObj.put("specInfo", specInfo);

                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                    shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsInventory.setPropertys(propertys);
                    shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                    int inventoryCount = shoppingGoodsInventory.getCount();
                    goodsObj.put("inventoryCount", inventoryCount);
                }
                //积分、余额等使用条件查询
                int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:限额积分抵扣；2：不限额积分抵扣
                int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
                int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingGoods.getUseMembershipSet();   //支持会员权益 0:不支持;1:支持
                goodsObj.put("useIntegralSet", useIntegralSet);
                goodsObj.put("useIntegralValue", useIntegralValue);
                goodsObj.put("useBalanceSet", useBalanceSet);
                goodsObj.put("useMembershipSet", useMembershipSet);

                goodsObj.put("goodsTransfee", shoppingGoods.getGoodsTransfee());  //运费承担类型0：买家承担；1：卖家承担；2：不支持快递
                goodsObj.put("selfextractionSet", shoppingGoods.getSelfextractionSet());  //是否支持自提0:不支持;1:支持
                goodsObj.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());  //自提地址

                goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP));

                //统计商品重量和计算运费信息
                if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                    //用户没有选择运输方式时，也默认使用快递,只有用户选择了自提时，才不需要计算运费
                    if(null == goodsOne.get("transport") || "".equals(goodsOne.get("transport"))||goodsOne.get("transport").equals("快递")){
                        //通过运费模板计算运费
                        if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                            BigDecimal goodsWeights = shoppingGoods.getGoodsWeight().multiply(new BigDecimal((goodsCount)));  //商品重量
                            Map<String, Object> objMap = new HashMap<>();
                            objMap.put("goodsId", goodsId);
                            objMap.put("goodsCount", goodsCount);
                            goodsTransList.add(objMap);
                            totalWeight = totalWeight.add(goodsWeights);
                        } else {
                            shipPrice = shipPrice.add(shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee());
                        }
                    }
                }

            } else if (cartType == Const.SHOPPING_ACT_CART_TYPE) {
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(goodsId);
                //活动主体信息
                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                BigDecimal currentPrice = artOrderUtil.getActivityCurrentPrice(shoppingArtactivity,propertys);

                goodsObj.put("goodsName", shoppingArtactivity.getActivityName());
                goodsObj.put("currentPrice", currentPrice);
                goodsObj.put("photoId", shoppingArtactivity.getMainPhotoId());
                goodsObj.put("limitBuy", shoppingArtactivity.getSignupPerLimit());
                if (StringUtil.isNotNull(propertys)) {
                    goodsObj.put("propertys", goodsOne.get("propertys"));
                    goodsObj.put("specInfo", specInfo);

                    ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                    shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                    shoppingArtactivityInventory.setPropertys(propertys);
                    shoppingArtactivityInventory = shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                    int inventoryCount = shoppingArtactivityInventory.getCount();
                    goodsObj.put("inventoryCount", inventoryCount);
                }
                //积分、余额等使用条件查询
                int useIntegralSet = shoppingArtactivity.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:限额积分抵扣；2：不限额积分抵扣
                int useIntegralValue = shoppingArtactivity.getUseIntegralValue();    //单个商品积分抵扣值
                int useBalanceSet = shoppingArtactivity.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingArtactivity.getUseMembershipSet();   //支持会员权益 0:不支持;1:支持

                goodsObj.put("useIntegralSet", useIntegralSet);
                goodsObj.put("useIntegralValue", useIntegralValue);
                goodsObj.put("useBalanceSet", useBalanceSet);
                goodsObj.put("useMembershipSet", useMembershipSet);

                //报名需要填写的信息项
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("activityId", goodsId);
                List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryActivityInfoList(reqMap);
                goodsObj.put("infos", infos);

                goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP));
            } else if (cartType == Const.SHOPPING_PLAN_CART_TYPE) {
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(goodsId);
                //活动主体信息
                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                BigDecimal currentPrice = artOrderUtil.getPlanCurrentPrice(shoppingArtplan,propertys);

                goodsObj.put("goodsName", shoppingArtplan.getActivityName());
                goodsObj.put("currentPrice", currentPrice);
                goodsObj.put("photoId", shoppingArtplan.getMainPhotoId());
                goodsObj.put("limitBuy", shoppingArtplan.getSignupPerLimit());
                if (StringUtil.isNotNull(propertys)) {
                    goodsObj.put("propertys", goodsOne.get("propertys"));
                    goodsObj.put("specInfo", specInfo);

                    ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                    shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                    shoppingArtplanInventory.setPropertys(goodsOne.getString("propertys"));
                    shoppingArtplanInventory = shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                    int inventoryCount = shoppingArtplanInventory.getCount();
                    goodsObj.put("inventoryCount", inventoryCount);
                }
                //积分、余额等使用条件查询
                int useIntegralSet = shoppingArtplan.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:限额积分抵扣；2：不限额积分抵扣
                int useIntegralValue = shoppingArtplan.getUseIntegralValue();    //单个商品积分抵扣值
                int useBalanceSet = shoppingArtplan.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingArtplan.getUseMembershipSet();   //支持会员权益 0:不支持;1:支持

                goodsObj.put("useIntegralSet", useIntegralSet);
                goodsObj.put("useIntegralValue", useIntegralValue);
                goodsObj.put("useBalanceSet", useBalanceSet);
                goodsObj.put("useMembershipSet", useMembershipSet);

                //报名需要填写的信息项
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("activityId", goodsId);
                List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryPlanInfoList(reqMap);
                goodsObj.put("infos", infos);
                goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP));
            } else if (cartType == Const.SHOPPING_CLASS_CART_TYPE) {
                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                shoppingArtclass.setId(goodsId);
                //主体信息
                shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
                //实时查询商品现价
                BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();

                goodsObj.put("goodsName", shoppingArtclass.getClassName());
                goodsObj.put("currentPrice", currentPrice);
                goodsObj.put("photoId", shoppingArtclass.getMainPhotoId());
                //积分、余额等使用条件查询
                int useIntegralSet = shoppingArtclass.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:限额积分抵扣；2：不限额积分抵扣
                int useIntegralValue = shoppingArtclass.getUseIntegralValue();    //单个商品积分抵扣值
                int useBalanceSet = shoppingArtclass.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingArtclass.getUseMembershipSet();   //支持会员权益 0:不支持;1:支持

                goodsObj.put("useIntegralSet", useIntegralSet);
                goodsObj.put("useIntegralValue", useIntegralValue);
                goodsObj.put("useBalanceSet", useBalanceSet);
                goodsObj.put("useMembershipSet", useMembershipSet);

                //报名需要填写的信息项
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("classId", goodsId);
                List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryClassInfoList(reqMap);
                goodsObj.put("infos", infos);

                goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            goodsArray.add(goodsObj);
        }
        multOrdernfo.setGoodsArray(goodsArray);
        multOrdernfo.setGoodsTransList(goodsTransList);
        multOrdernfo.setGoodsAmount(goodsPrice);
        multOrdernfo.setShipAmount(shipPrice);
    }

    /**
     * 校验订单中的购物车信息是否可用
     * @param goodsArray 商品信息
     * @return true:可用；false:不可用
     */
    public Boolean checkCartAvailable(JSONArray goodsArray){
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            if(StringUtil.isNotNull(goodsObj.get("cartId"))){
                String cartId = goodsObj.getString("cartId");
                ShoppingGoodscart goodscart = new ShoppingGoodscart();
                goodscart.setId(cartId);
                goodscart = shoppingGoodscartDao.queryDetail(goodscart);
                if(StringUtil.isNotNull(goodscart.getOfId())){   //有订单id则表示该购物车信息已经被提交
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取用户收货地址
     * @param goodsTransList
     * @param addressId
     * @param mzUserId
     * @return 收货地址，goodsTransList为空时直接返回null,不需要查询收货地址
     */
    public JSONObject getUserAddress(List<Map<String, Object>> goodsTransList,String addressId, String mzUserId){
        if(goodsTransList.isEmpty()){
            return null;
        }
        return getUserAddress(addressId,mzUserId);
    }

    /**
     * 订单金额计算
     * @param multOrdernfo 订单信息
     * @param userAccountInfo 用户账户信息
     * @return 订单信息
     */
    public MultOrdernfo countOrderAmount(MultOrdernfo multOrdernfo, UserAccountInfo userAccountInfo){
        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = multOrdernfo.getUserId();

        //订单运费计算
        BigDecimal shipAmount = multOrdernfo.getShipAmount();
        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = multOrdernfo.getGoodsAmount();
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = shipAmount.add(goodsAmount);
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;
//        BigDecimal totalCashPrice = BigDecimal.ZERO;

        multOrdernfo.setShipAmount(shipAmount);
        multOrdernfo.setGoodsAmount(goodsAmount);
        multOrdernfo.setTotalAmount(totalAmount);
        //订单商品
        JSONArray goodsArray =multOrdernfo.getGoodsArray();
        //定额积分抵扣的情况需要特殊处理，因此先获取到定额积分和剩下的现金支付的数额
        int fixedIntegalValue = 0;
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            BigDecimal currentPrice = goodsObj.getBigDecimal("currentPrice");
            int goodsCount = goodsObj.getInteger("goodsCount");
            goodsObj.put("perGoodsPrice", currentPrice.multiply(new BigDecimal(goodsCount)));
            goodsObj.put("perPayPrice", currentPrice.multiply(new BigDecimal(goodsCount)));
            if (goodsObj.getInteger("useIntegralSet") == 1) {  //定额积分抵扣
//                integralOn =true;
                int useIntegralValue = goodsObj.getInteger("useIntegralValue");
                if(currentPrice.multiply(new BigDecimal((100))).intValue()<useIntegralValue){
                    useIntegralValue = currentPrice.multiply(new BigDecimal((100))).intValue();
                }
                goodsObj.put("useIntegralValue", useIntegralValue);
                fixedIntegalValue = fixedIntegalValue + useIntegralValue * goodsCount;
                goodsObj.put("perIntegralValue", useIntegralValue * goodsCount);
                //现价减去定额积分抵扣的额度，即为剩下的需要支付的现金额度
                BigDecimal cashPrice = (currentPrice.multiply(new BigDecimal(goodsCount))).subtract(new BigDecimal(useIntegralValue * goodsCount).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP));
                goodsObj.put("perPayPrice", cashPrice);
//                totalCashPrice = totalCashPrice.add(cashPrice);
            }
        }

        //从CRM获取用户选择的优惠券详情
        ShoppingCoupon shoppingCoupon = getUserCoupon(multOrdernfo.getCouponId());
        //查询商品可用的可用优惠券
        List<String> couponIds = new ArrayList<>();
        JSONArray couponArray= CommonUtil.getCartsCouppon(multOrdernfo.getUserId(),goodsArray);
        multOrdernfo.setCouponArray(couponArray);
        for(int i=0;i<couponArray.size();i++){
            JSONObject obj = couponArray.getJSONObject(i);
            couponIds.add(obj.getString("id"));
        }
        //判断用户选择的优惠券是否是有效的优惠券，防止优惠券被篡改
        if(StringUtil.isNotNull(multOrdernfo.getCouponId())&&(shoppingCoupon == null || !couponIds.contains(multOrdernfo.getCouponId()))){
            multOrdernfo.setErrorMsg("当前选择的优惠券信息不存在！");
            return multOrdernfo;
        }
        HashSet<String> goodsSet = new HashSet<>();
        for(int i=0;i<couponArray.size();i++){
            JSONObject obj = couponArray.getJSONObject(i);
            if(shoppingCoupon.getRight_No().equals(obj.getString("right_No"))){
                goodsSet = (HashSet<String>) obj.get("goodsSet");
                break;
            }
        }
        //折扣优惠
        if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("memberDiscount")) {
            //如果是折扣优惠券，需要计算折扣
            //折扣优惠券只能对该优惠券关联的范围商品使用折扣
            int cutIntegral = 0;
            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
            for (int i = 0; i < goodsArray.size(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                if (goodsSet.contains(goodsObj.getString("cartType")+"&"+goodsObj.getString("goodsId"))) {
                    BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                    BigDecimal cprice = perPayPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                    goodsObj.put("perPayPrice", cprice);
                    BigDecimal perCouponCut = perPayPrice.subtract(cprice);
                    //定额积分值也要参与折扣
                    if (goodsObj.getInteger("useIntegralSet") == 1) {
                        int perIntegralValue = goodsObj.getInteger("perIntegralValue");
                        int cIntegralValue = new BigDecimal(perIntegralValue).multiply(discount).intValue();
                        goodsObj.put("perIntegralValue", cIntegralValue);
                        perCouponCut = perCouponCut.add(new BigDecimal(perIntegralValue - cIntegralValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP));
                        cutIntegral = cutIntegral + (perIntegralValue - cIntegralValue);
                    }
                    goodsObj.put("perCouponCut", perCouponCut);
                    couponCut = couponCut.add(perCouponCut);
                } else {
                    goodsObj.put("perCouponCut", BigDecimal.ZERO);
                }
            }
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
            //定额积分值也要参与折扣
            fixedIntegalValue = fixedIntegalValue - cutIntegral;
        }
        //抵扣优惠
        else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("coincp")) {
            //如果是满减优惠券或者代金券，直接抵扣相应金额
            //满减优惠券只能对该优惠券关联的范围商品使用，因此需要计算能使用该优惠券的商品的现金总额couponPrice,
            //而不是直接使用上面的payPrice，以避免抵扣的金额超过couponPrice
            BigDecimal couponPrice = BigDecimal.ZERO;
            for (int i = 0; i < goodsArray.size(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                if (goodsSet.contains(goodsObj.getString("cartType")+"&"+goodsObj.getString("goodsId"))) {
                    BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                    couponPrice = couponPrice.add(perPayPrice);
                }
            }
            BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()));
            //如果参加抵扣的商品的现金总额couponPrice小于抵扣券的金额couponAmount，则取couponPrice做实际的抵扣金额
            couponAmount = couponPrice.compareTo(couponAmount)<0?couponPrice:couponAmount;

            if(couponPrice.compareTo(BigDecimal.ZERO)==1){
                BigDecimal totalPerCouponCut = BigDecimal.ZERO;
                for (int i = 0; i < goodsArray.size(); i++) {
                    JSONObject goodsObj = goodsArray.getJSONObject(i);

                    if (goodsSet.contains(goodsObj.getString("cartType")+"&"+goodsObj.getString("goodsId"))) {
                        BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                        //计算分配到每个商品上的优惠值
                        BigDecimal perCouponCut =BigDecimal.ZERO;
                        if(i!=goodsArray.size()-1){
                            perCouponCut = couponAmount.multiply(perPayPrice).divide(couponPrice,2, RoundingMode.HALF_UP);
                            totalPerCouponCut=totalPerCouponCut.add(perCouponCut);
                        }else{
                            perCouponCut = couponAmount.subtract(totalPerCouponCut);
                        }

                        goodsObj.put("perPayPrice", perPayPrice.subtract(perCouponCut));
                        goodsObj.put("perCouponCut", perCouponCut);
                    } else {
                        goodsObj.put("perCouponCut", BigDecimal.ZERO);
                    }
                }
                couponCut = couponAmount;
                //剩余需要支付的商品金额
                payAmount = payAmount.subtract(couponCut);
            }
        }
        //优惠券抵扣金额
        multOrdernfo.setCouponCut(couponCut);

        //会员等级折扣
        BigDecimal memberDiscount = CommonUtil.getUserMemberShip(userId);
        int cutIntegral = 0;
        //获取支持会员权益的商品id集合
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            if (goodsObj.getInteger("useMembershipSet") == 1) {
                ids.add(goodsObj.getString("goodsId"));
            }

        }
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            if (ids.contains(goodsObj.getString("goodsId"))) {
                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                BigDecimal cprice = perPayPrice.multiply(memberDiscount).setScale(2, BigDecimal.ROUND_HALF_UP);
                goodsObj.put("perPayPrice", cprice);
                BigDecimal perAccountCut = perPayPrice.subtract(cprice);

                if (goodsObj.getInteger("useIntegralSet") == 1) {
                    int perIntegralValue = goodsObj.getInteger("perIntegralValue");
                    int cIntegralValue = new BigDecimal(perIntegralValue).multiply(memberDiscount).intValue();
                    goodsObj.put("perIntegralValue", cIntegralValue);
                    perAccountCut = perAccountCut.add(new BigDecimal(perIntegralValue - cIntegralValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP));
                    cutIntegral = cutIntegral + (perIntegralValue - cIntegralValue);
                }
                goodsObj.put("perAccountCut", perAccountCut);
                accountCut = accountCut.add(perAccountCut);
            } else {
                goodsObj.put("perAccountCut", BigDecimal.ZERO);
            }
        }
        payAmount = payAmount.subtract(accountCut);
        fixedIntegalValue = fixedIntegalValue - cutIntegral;
        multOrdernfo.setAccountCut(accountCut);

        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额

        if(fixedIntegalValue>accountPoint){
            multOrdernfo.setErrorMsg("当前账户积分不足！");
            return multOrdernfo;
        }
        if(fixedIntegalValue >pointLimitPay){
            multOrdernfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
            return multOrdernfo;
        }
        int maxIntegralValue = fixedIntegalValue;
        //参与限额抵扣的商品总价
        BigDecimal tPrice = BigDecimal.ZERO;
        //参与限额抵扣的商品总积分上限
        int tIntegral=0;
        //用户选择使用积分支付
        if (multOrdernfo.isUseIntegral()) {
            for (int i = 0; i < goodsArray.size(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                int useIntegralSet = goodsObj.getInteger("useIntegralSet");
                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                if (useIntegralSet == 2) { //限额积分抵扣
                    tPrice = tPrice.add(perPayPrice);

                    int perPayToInt =perPayPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                    int goodsCount = goodsObj.getInteger("goodsCount");
                    int useIntegralValue = goodsObj.getInteger("useIntegralValue");
                    int perMaxIntegralValue = useIntegralValue * goodsCount<perPayToInt?useIntegralValue * goodsCount:perPayToInt;
                    tIntegral = tIntegral+perMaxIntegralValue;
                    goodsObj.put("perMaxIntegralValue",perMaxIntegralValue);
                    maxIntegralValue = maxIntegralValue + perMaxIntegralValue;
                }
            }
        }
//        int pointLimit = accountPoint;   //账户积分
//        if(pointPay>0){
//            pointLimit = pointPay>pointLimit?pointLimit:pointPay;
//        }
//        if (maxIntegralValue > pointLimit) {
//            maxIntegralValue = pointLimit;
//        }
        //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
        integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        //计算平均分配到每个商品上的积分抵扣值
        //先统计不限额积分抵扣的商品数量
        int iNum=0;
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            int useIntegralSet = goodsObj.getInteger("useIntegralSet");
            if (useIntegralSet == 2) {
                iNum++;
            }
        }
        int iCount=0;
        int totalPerIntegralCutValue=0;
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            int useIntegralSet = goodsObj.getInteger("useIntegralSet");
            BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
            if (useIntegralSet == 2) {
                iCount++;
                if (tPrice.compareTo(BigDecimal.ZERO) == 1) {
                    if(iCount!=iNum){
                        int perMaxIntegralValue = goodsObj.getInteger("perMaxIntegralValue");
                        int perIntegralCutValue = (new BigDecimal(integralValue - fixedIntegalValue).multiply(new BigDecimal(perMaxIntegralValue))).divide(new BigDecimal(tIntegral),2, RoundingMode.HALF_UP).intValue();
                        totalPerIntegralCutValue=totalPerIntegralCutValue+perIntegralCutValue;
                        goodsObj.put("perIntegralCutValue", perIntegralCutValue);
                        BigDecimal perIntegralCut = new BigDecimal(perIntegralCutValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP);
                        goodsObj.put("perIntegralCut", perIntegralCut);
                        goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                    }else{
                        int perIntegralCutValue = (integralValue - fixedIntegalValue)-totalPerIntegralCutValue;
                        goodsObj.put("perIntegralCutValue", perIntegralCutValue);
                        BigDecimal perIntegralCut = new BigDecimal(perIntegralCutValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP);
                        goodsObj.put("perIntegralCut", perIntegralCut);
                        goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                    }

                }
            } else if (useIntegralSet == 1) {
                int perIntegralValue = goodsObj.getInteger("perIntegralValue");
                goodsObj.put("perIntegralCutValue", perIntegralValue);
                BigDecimal perIntegralCut = new BigDecimal(perIntegralValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP);
                goodsObj.put("perIntegralCut", perIntegralCut);
            } else {
                goodsObj.put("perIntegralCutValue", 0);
                goodsObj.put("perIntegralCut", BigDecimal.ZERO);
            }
        }
        multOrdernfo.setMinIntegralValue(fixedIntegalValue);
        multOrdernfo.setMaxIntegralValue(integralValue);

        //将积分根据比例转换为相应的金额
        integralCut = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale),2, RoundingMode.HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = payAmount.subtract(integralCut);

        multOrdernfo.setIntegralValue(integralValue);
        multOrdernfo.setIntegralCut(integralCut);

        //运费可以用余额支付，因此在计算余额抵扣时，加上运费
        tPrice = BigDecimal.ZERO;
        //用户选择扣除的余额
        if (multOrdernfo.isUseBalance()) {
            for (int i = 0; i < goodsArray.size(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                int useBalanceSet = goodsObj.getInteger("useBalanceSet");
                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                if (useBalanceSet == 1) {
                    tPrice = tPrice.add(perPayPrice);
                }
            }

            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;

            //计算平均分配到每个商品上的余额抵扣值
            for (int i = 0; i < goodsArray.size(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                int useBalanceSet = goodsObj.getInteger("useBalanceSet");
                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                if (useBalanceSet == 1) {
                    if (tPrice.compareTo(BigDecimal.ZERO) == 1) {
                        //按比例计算每个商品抵扣的余额值
                        BigDecimal cent =  perPayPrice.divide(tPrice,2, RoundingMode.HALF_UP);
                        BigDecimal perBalanceCut = balanceCut.multiply(cent);
                        goodsObj.put("perBalanceCut", perBalanceCut);
                        goodsObj.put("perPayPrice", perPayPrice.subtract(perBalanceCut));
                    }
                } else {
                    goodsObj.put("perBalanceCut", BigDecimal.ZERO);
                }
            }
            multOrdernfo.setBalanceCut(balanceCut);
            payAmount = payAmount.subtract(balanceCut);

            if (balanceCut.compareTo(balanceLimit) == -1) {
                //账户剩余的余额大于运费,则运费全部用余额支付
                if (shipAmount.compareTo(balanceLimit.subtract(balanceCut)) <=0) {
                    balanceCut = balanceCut.add(shipAmount);
                } else {
                    balanceCut = balanceLimit;
                    payAmount = payAmount.add(shipAmount.subtract(balanceLimit.subtract(balanceCut)));
                }

            } else {
                payAmount = payAmount.add(shipAmount);
            }
        } else {
            payAmount = payAmount.add(shipAmount);
        }
        multOrdernfo.setBalanceCut(balanceCut);

        multOrdernfo.setPayAmount(payAmount);

        multOrdernfo.setCountSuccess(true);
        return multOrdernfo;
    }

    /**
     * 扣减订单商品库存
     * @param goodsArray 商品信息
     * @return true：库存扣减成功；false：库存扣减失败
     */
    public boolean cutStock(JSONArray goodsArray){
        boolean flag = true;
        JSONArray doneArray = new JSONArray();
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            String goodsId = goodsObj.getString("goodsId");
            String propertys = goodsObj.getString("propertys");
            int goodsCount = goodsObj.getInteger("goodsCount");
            int cartType = goodsObj.getInteger("cartType");
            if (cartType == Const.SHOPPING_CUL_CART_TYPE || cartType == Const.SHOPPING_INT_CART_TYPE) {
                if(shoppingOrderUtil.cutGoodsStock(goodsId,propertys,goodsCount)>=0){
                    doneArray.add(goodsObj);
                }else{
                    flag=false;
                    break;
                }
            } else if (cartType == Const.SHOPPING_ACT_CART_TYPE) {
                if(artOrderUtil.cutActStock(goodsId,propertys,goodsCount)>=0){
                    doneArray.add(goodsObj);
                }else{
                    flag=false;
                    break;
                }
            }else if (cartType == Const.SHOPPING_PLAN_CART_TYPE) {
                if(artOrderUtil.cutPLanStock(goodsId,propertys,goodsCount)>=0){
                    doneArray.add(goodsObj);
                }else{
                    flag=false;
                    break;
                }
            }
        }
        for (int i = 0; i < doneArray.size(); i++) {
            JSONObject goodsObj = doneArray.getJSONObject(i);
            String goodsId = goodsObj.getString("goodsId");
            String propertys = goodsObj.getString("propertys");
            int goodsCount = goodsObj.getInteger("goodsCount");
            int cartType = goodsObj.getInteger("cartType");
            if (cartType == Const.SHOPPING_CUL_CART_TYPE || cartType == Const.SHOPPING_INT_CART_TYPE) {
                shoppingOrderUtil.addGoodsStock(goodsId,propertys,goodsCount);
            } else if (cartType == Const.SHOPPING_ACT_CART_TYPE) {
                artOrderUtil.addActStock(goodsId,propertys,goodsCount);
            }else if (cartType == Const.SHOPPING_PLAN_CART_TYPE) {
                artOrderUtil.addPlanStock(goodsId,propertys,goodsCount);
            }
        }
        return flag;
    }

    /**
     * 检验订单中是否有商品超出限购
     * @param multOrdernfo 订单信息
     * @return true：超过限购；false：未超出限购
     */
    public boolean upLimitBuy(MultOrdernfo multOrdernfo){
        JSONArray goodsArray = multOrdernfo.getGoodsArray();
        String userId = multOrdernfo.getUserId();
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            String goodsId = goodsObj.getString("goodsId");
            int goodsCount = goodsObj.getInteger("goodsCount");
            int cartType = goodsObj.getInteger("cartType");
            if(null !=goodsObj.get("limitBuy")&&upLimitBuy(goodsId,cartType,goodsObj.getInteger("limitBuy"),goodsCount,userId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 多个商品合并运费计算
     * @param goodsTransList 商品列表
     * @param addObj 地址
     * @return 运费
     */
    public BigDecimal buildShipPrice(List<Map<String, Object>> goodsTransList,JSONObject addObj) {
        ShoppingTransport shoppingTransport = new ShoppingTransport();
        ShoppingGoods shoppingGoods = new ShoppingGoods();
        shoppingGoods.setId(goodsTransList.get(0).get("goodsId").toString());
        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
        shoppingTransport.setId(shoppingGoods.getTransportId());
        shoppingTransport = shoppingTransportDao.queryDetail(shoppingTransport);

        String transInfo = shoppingTransport.getTransEmsInfo() == null ? (shoppingTransport.getTransExpressInfo()==null?shoppingTransport.getTransMailInfo():shoppingTransport.getTransExpressInfo()) : shoppingTransport.getTransEmsInfo();

        BigDecimal shipPrice = BigDecimal.ZERO;

        if (shoppingTransport.getTransType() == 3){  //按地区收费
            if(addObj==null){
                shipPrice=null;
            }else{
                String cityCode = addObj.getString("city_code");
                String provinceCode = addObj.getString("province_code");
                BigDecimal areaPrice =shoppingTransportDao.queryAreaPrice(cityCode);
                if(areaPrice==null){
                    areaPrice =shoppingTransportDao.queryAreaPrice(provinceCode);
                }
                shipPrice = areaPrice==null?new BigDecimal(100):areaPrice;
            }
        }
        else if (shoppingTransport.getTransType() == 1){//按重量计费
            BigDecimal totalWeight = BigDecimal.ZERO;
            for(Map<String, Object> goodsMap:goodsTransList){
                ShoppingGoods goods = new ShoppingGoods();
                goods.setId(goodsMap.get("goodsId").toString());
                goods = shoppingGoodsDao.queryDetail(goods);
                int goodsCount = Integer.valueOf(goodsMap.get("goodsCount").toString());
                BigDecimal goodsWeights = goods.getGoodsWeight().multiply(new BigDecimal((goodsCount)));  //商品重量
                totalWeight = totalWeight.add(goodsWeights);
            }
            if(totalWeight.compareTo(BigDecimal.ZERO)==0)
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
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight),2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }else if (shoppingTransport.getTransType() == 2){//按体积计费
            BigDecimal totalVolume = BigDecimal.ZERO;
            for(Map<String, Object> goodsMap:goodsTransList){
                ShoppingGoods goods = new ShoppingGoods();
                goods.setId(goodsMap.get("goodsId").toString());
                goods = shoppingGoodsDao.queryDetail(goods);
                int goodsCount = Integer.valueOf(goodsMap.get("goodsCount").toString());
                BigDecimal goodsVolumes = goods.getGoodsVolume().multiply(new BigDecimal((goodsCount)));  //商品体积
                totalVolume = totalVolume.add(goodsVolumes);
            }
            if(totalVolume.compareTo(BigDecimal.ZERO)==0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首体积
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalVolume.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首重/首体积
                            shipPrice = transFee;
                        } else {   //超过首重/首体积
                            BigDecimal diffWeight = totalVolume.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight),2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }else if (shoppingTransport.getTransType() == 0){//按数量计费
            int totalCount = 0;
            for(Map<String, Object> goodsMap:goodsTransList){
                totalCount = totalCount+Integer.valueOf(goodsMap.get("goodsCount").toString());
            }
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首体积
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalCount<=transWeight) {  //未超过首重/首体积
                            shipPrice = transFee;
                        } else {   //超过首重/首体积
                            BigDecimal diffWeight = new BigDecimal(totalCount-transWeight);
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight),2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        return shipPrice;
    }
}
