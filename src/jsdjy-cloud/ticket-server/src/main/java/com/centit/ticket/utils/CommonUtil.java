package com.centit.ticket.utils;

import com.centit.ticket.po.*;

import java.util.*;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {

    /**
     * 获取官方默认店铺
     */
    public static ShoppingStore getSystemStore() {
        ShoppingStore shoppingStore = new ShoppingStore();
        shoppingStore.setId("1");
        return CommonInit.staticShoppingStoreDao.queryDetail(shoppingStore);
    }

    /**
     * 获取店铺信息
     */
    public static ShoppingStore getStoreInfo(String storeId) {
        if(null !=storeId){
            ShoppingStore shoppingStore = new ShoppingStore();
            shoppingStore.setId(storeId);
            return CommonInit.staticShoppingStoreDao.queryDetail(shoppingStore);
        }
        return null;
    }

    /**
     * 获取用户购物车id
     */
    public static String getUserScId(String userId) {
        String goodsStoreId = getSystemStore().getId();  //默认商户id
        //商户购物车信息
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("userId", userId);
        reqMap.put("storeId", goodsStoreId);
        reqMap.put("deleteStatus", '0');
        List<ShoppingStorecart> shoppingStorecartList = CommonInit.staticshoppingStorecartDao.queryList(reqMap);
        ShoppingStorecart shoppingStorecart = new ShoppingStorecart();
        if (shoppingStorecartList.isEmpty()) {
            shoppingStorecart.setUserId(userId);
            shoppingStorecart.setStoreId(goodsStoreId);
            CommonInit.staticshoppingStorecartDao.insert(shoppingStorecart);
        } else {
            shoppingStorecart = shoppingStorecartList.get(0);
        }

        return shoppingStorecart.getId();
    }

    /**
     * 根据用户名获取用户
     */
    public static ShoppingUser getShoppingUserByName(String userName){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("userName",userName);
        List<ShoppingUser> users=CommonInit.staticShoppingUserDao.queryList(reqMap);
        if(!users.isEmpty()){
            return users.get(0);
        }
        return null;
    }

    /**
     * 根据userId获取用户
     */
    public static ShoppingUser getShoppingUserByUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        return CommonInit.staticShoppingUserDao.queryDetail(user);
    }

    /**
     * 根据userId获取用户麦座id
     */
    public static String getMzUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user=CommonInit.staticShoppingUserDao.queryDetail(user);
//        if(user==null||user.getMzuserid()==null||"".equals(user.getMzuserid())){
//            return "9253315";
//        }else{
//            return user.getMzuserid();
//        }
        return user.getMzuserid();

    }

    /**
     * 根据名称获取分类Id
     */
    public static String getClassId(String className){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("className",className);
        List<TicketClass> classes=CommonInit.staticTicketClassDao.queryList(reqMap);
        if(!classes.isEmpty()){
            return classes.get(0).getClassId();
        }
        return null;
    }

    /**
     * 获取会员资产限额规则
     *
     */
    public static ShoppingAssetRule getAssetRule(){
        HashMap<String, Object> reqMap = new HashMap<>();
        List<ShoppingAssetRule> limits= CommonInit.staticShoppingAssetRuleDao.queryList(reqMap);
        if(limits.size()==1){
            return limits.get(0);
        }
        return null;

    }

    /**
     * 保存第三方请求日志
     *
     */
    public static void addThirdLog(int status,int logType,String logInfo,String reqmethod,String serverpath,String reqtime,
                                   String reqinfo,String rettime,String retinfo){
        TLmThirdlog thirdlog = new TLmThirdlog();
        thirdlog.setLogtype(logType);
        thirdlog.setLoginfo(logInfo);
        thirdlog.setLogip(StringUtil.getLocalIp());
        thirdlog.setReqmethod(reqmethod);
        thirdlog.setServerpath(serverpath);
        thirdlog.setReqtime(reqtime);
        thirdlog.setReqinfo(reqinfo);
        thirdlog.setRettime(rettime);
        thirdlog.setRetinfo(retinfo);
        thirdlog.setStatus(status);
        CommonInit.staticTLmThirdlogDao.insert(thirdlog);
    }
}