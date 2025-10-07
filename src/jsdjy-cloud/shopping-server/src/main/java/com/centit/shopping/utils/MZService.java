package com.centit.shopping.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.po.TicketCouponBind;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class MZService {
    private static final Logger log = LoggerFactory.getLogger(MZService.class);

    public static TaobaoClient getClient() {
        return new DefaultTaobaoClient(Const.MZ_CLIENT_URL, Const.MZ_CLIENT_APPKEY, Const.MZ_CLIENT_SECRET);
    }

    /**
     * @描述: 获取积分和余额免密限额信息
     * @参数: [userId 用户id]
     */
    public static JSONObject getAssetRule() {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetRuleGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();
            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_rule_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取账户资产信息
     * @参数: [userId 用户id]
     */
    public static JSONObject getAssetinfo(String mzUserId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAssetinfoGetRequest req = new AlibabaDamaiMzUserAssetinfoGetRequest();
        req.setMzUserId(mzUserId);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetinfoGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_assetinfo_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取账户资产信息", "POST", "AlibabaDamaiMzUserAssetinfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取账户资产信息", "POST", "AlibabaDamaiMzUserAssetinfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取账户资产信息", "POST", "AlibabaDamaiMzUserAssetinfoGetRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取麦座订单性情
     * @参数: [userId 用户id]
     */
    public static JSONObject getMzOrderDetail(String mz_user_id, String mz_order_id) {
        TaobaoClient client = getClient();
        AlibabaDamaiMzOrderDetailRequest req = new AlibabaDamaiMzOrderDetailRequest();
        req.setMzOrderId(mz_order_id);
        req.setMzUserId(mz_user_id);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzOrderDetailResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_detail_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取麦座订单性情", "POST", "AlibabaDamaiMzOrderDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取麦座订单性情", "POST", "AlibabaDamaiMzOrderDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取麦座订单性情", "POST", "AlibabaDamaiMzOrderDetailRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 取消订单
     * @参数: [userId 用户id]
     */
    public static Boolean cancelOrder(String mzUserId, String mz_order_id, String cancel_reason) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzOrderCancelRequest req = new AlibabaDamaiMzOrderCancelRequest();
        req.setMzUserId(mzUserId);
        req.setMzOrderId(mz_order_id);
        if (cancel_reason == null || cancel_reason.trim().equals("")) {
            cancel_reason = "用户手动取消订单";
        }
        req.setCancelReason(cancel_reason);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzOrderCancelResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_cancel_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "取消订单", "POST", "AlibabaDamaiMzOrderCancelRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "取消订单", "POST", "AlibabaDamaiMzOrderCancelRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "取消订单", "POST", "AlibabaDamaiMzOrderCancelRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 获取会员收货地址列表
     * @参数: [userId 用户id]
     */
    public static JSONObject getUserAddress(String mzUserId, int pageSize, int page) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressListRequest req = new AlibabaDamaiMzUserAddressListRequest();
        req.setMzUserId(mzUserId);
        req.setPageSize(Long.valueOf(pageSize));
        req.setPage(Long.valueOf(page));
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAddressListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址列表", "POST", "AlibabaDamaiMzUserAddressListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址列表", "POST", "AlibabaDamaiMzUserAddressListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址列表", "POST", "AlibabaDamaiMzUserAddressListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取会员收货地址详情
     * @参数: [userId 用户id]
     */
    public static JSONObject getAddressDetail(String mzUserId, String addressId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressDetailRequest req = new AlibabaDamaiMzUserAddressDetailRequest();
        req.setMzUserId(mzUserId);
        req.setAddressId(addressId);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAddressDetailResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_detail_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取票凭证列表
     * @参数: [userId 用户id]
     */
    public static JSONObject getTicketList(String mzUserId, String mz_order_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzTicketListRequest req = new AlibabaDamaiMzTicketListRequest();
        req.setMzOrderId(mz_order_id);
        req.setMzUserId(mzUserId);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzTicketListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_ticket_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取票凭证列表", "POST", "AlibabaDamaiMzTicketListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data_list");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取票凭证列表", "POST", "AlibabaDamaiMzTicketListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取票凭证列表", "POST", "AlibabaDamaiMzTicketListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取会员资产变更记录列表
     * @参数: [userId 用户id]
     */
    public static JSONObject getAssetRecordList(String mzUserId, int asset_type, String start_time, String end_time, int page, int page_size) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAssetRecordListRequest req = new AlibabaDamaiMzUserAssetRecordListRequest();
        req.setMzUserId(mzUserId);
        req.setAssetType(Long.valueOf(asset_type));
        req.setEndTime(end_time);
        req.setStartTime(start_time);
        req.setPageSize(Long.valueOf(page_size));
        req.setPage(Long.valueOf(page));
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetRecordListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_record_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取会员资产变更记录列表", "POST", "AlibabaDamaiMzUserAssetRecordListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员资产变更记录列表", "POST", "AlibabaDamaiMzUserAssetRecordListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员资产变更记录列表", "POST", "AlibabaDamaiMzUserAssetRecordListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 添加会员积分
     * @参数: [userId 用户id]
     */
    public static boolean addPoint(String mzUserId, int integralValue, String assetBizKey, String businessId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
        req.setMzUserId(mzUserId);
        req.setAssetType(1L);  //资产类型： 1=积分 2=余额
        req.setChangeType(1L);  //变更类型 1 增加，2 减少
        req.setChangeValue(Long.valueOf(integralValue));
        req.setBusinessId(businessId);
        req.setChangeReason("江苏大剧院APP用户领取账户积分");
        req.setAssetBizKey(assetBizKey);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "添加会员积分", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "添加会员积分", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "添加会员积分", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 添加会员余额
     * @参数: [userId 用户id]
     */
    public static boolean addMoney(String mzUserId, int changeValue, String assetBizKey, String businessId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
        req.setMzUserId(mzUserId);
        req.setAssetType(2L);  //资产类型： 1=积分 2=余额
        req.setChangeType(1L);  //变更类型 1 增加，2 减少
        req.setChangeValue(Long.valueOf(changeValue));
        req.setBusinessId(businessId);
        req.setChangeReason("江苏大剧院APP用户领取账户余额");
        req.setAssetBizKey(assetBizKey);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "添加会员余额", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "添加会员余额", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "添加会员余额", "POST", "AlibabaDamaiMzUserAssetModifyRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 获取订单列表
     * @参数:
     */
    public static JSONArray getorderList(String mzUserId) {
        JSONArray resArray = new JSONArray();
        try {
            TaobaoClient client = getClient();
            AlibabaDamaiMzOrderListRequest req = new AlibabaDamaiMzOrderListRequest();
            boolean isend = false;
            Long page = 1L;
            while (!isend) {
                req.setPage(page);
                req.setPageSize(10L);
                req.setMzUserId(mzUserId);
                String reqtime = StringUtil.nowTimeMilesString();
                try {
                    AlibabaDamaiMzOrderListResponse rsp = client.execute(req);
                    String rettime = StringUtil.nowTimeMilesString();

                    log.info(rsp.getBody());
                    try {
                        JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_list_response").getJSONObject("result");
                        CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取订单列表", "POST", "AlibabaDamaiMzOrderListRequest",
                                reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                        if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                            JSONObject dataObj = resObj.getJSONObject("data");
                            Long total_page = dataObj.getLongValue("total_page");
                            JSONObject data_list = dataObj.getJSONObject("data_list");
                            if (null != data_list.get("order_v_o")) {
                                resArray.addAll(data_list.getJSONArray("order_v_o"));
                            }
                            if (page >= total_page) {
                                isend = true;
                            } else {
                                page = page + 1;
                            }
                        } else {
                            resArray = null;
                            break;
                        }
                    } catch (Exception e) {
                        CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取订单列表", "POST", "AlibabaDamaiMzOrderListRequest",
                                reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                        e.printStackTrace();
                        resArray = null;
                        break;
                    }

                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeMilesString();
                    CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取订单列表", "POST", "AlibabaDamaiMzOrderListRequest",
                            reqtime, req.getTextParams().toString(), rettime, e.toString());
                    e.printStackTrace();
                    return resArray;
                }
            }

            return resArray;
        } catch (Exception e) {
            e.printStackTrace();
            return resArray;
        }
    }

    /**
     * @描述: 获取场次信息
     * @参数:
     */
    public static String getProjectEvent(String project_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzEventListRequest req = new AlibabaDamaiMzEventListRequest();
        AlibabaDamaiMzEventListRequest.EventListParam obj1 = new AlibabaDamaiMzEventListRequest.EventListParam();
        obj1.setProjectId(project_id);
        req.setParam(obj1);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzEventListResponse rsp = client.execute(req);
            log.info(rsp.getBody());
            return rsp.getBody();
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取場次信息", "POST", "AlibabaDamaiMzOrderListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return e.toString();
        }



    }

    /**
     * @描述: 获取用户优惠券
     * @参数:
     */
    public static JSONObject getUserCouponList(String mzUserId, int pageNo, int pageSize) {
        JSONObject resObject = new JSONObject();
        TaobaoClient client = getClient();
        AlibabaDamaiMzUserCouponcodeListRequest req = new AlibabaDamaiMzUserCouponcodeListRequest();
        req.setPage(Long.valueOf(String.valueOf(pageNo)));
        req.setPageSize(Long.valueOf(String.valueOf(pageSize)));
        req.setMzUserId(mzUserId);
        req.setCouponCodeState(1L);  //获取可用优惠券
        String reqtime = StringUtil.nowTimeMilesString();
        try {

            AlibabaDamaiMzUserCouponcodeListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();
            try {

                log.info(rsp.getBody());
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_couponcode_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取用户优惠码", "POST", "AlibabaDamaiMzUserCouponcodeListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    JSONObject dataObj = resObj.getJSONObject("data");
                    int total = Integer.valueOf(dataObj.get("total_row").toString());
                    JSONObject data_list = dataObj.getJSONObject("data_list");
                    resObject.put("total", total);
                    resObject.put("objList", data_list.getJSONArray("user_coupon_code_v_o"));
                } else {
                    resObject = null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取用户优惠码", "POST", "AlibabaDamaiMzUserCouponcodeListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                resObject = null;
            }
            return resObject;
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取用户优惠码", "POST", "AlibabaDamaiMzUserCouponcodeListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 绑定优惠码
     * @参数: [userId 用户id]
     */
    public static Boolean bindCoupon(String phone, String mzUserId, String code_promotion_id) {
        TaobaoClient client = getClient();
        AlibabaDamaiMzUserCouponcodeBindRequest req = new AlibabaDamaiMzUserCouponcodeBindRequest();
        req.setMzUserId(mzUserId);
        req.setCodePromotionId(code_promotion_id);
        String reqtime = StringUtil.nowTimeMilesString();
        TicketCouponBind ticketCouponBind = new TicketCouponBind();
        ticketCouponBind.setPhone(phone);
        ticketCouponBind.setCodePromotionId(code_promotion_id);
        try {
            AlibabaDamaiMzUserCouponcodeBindResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_couponcode_bind_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "绑定优惠码", "POST", "AlibabaDamaiMzUserCouponcodeBindRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    ticketCouponBind.setMsg("success");
                    CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
                    return true;
                } else {
                    ticketCouponBind.setMsg(rsp.getBody());
                    CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
                    return false;
                }
            }catch (Exception e) {
                ticketCouponBind.setMsg(rsp.getBody());
                CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "绑定优惠码", "POST", "AlibabaDamaiMzUserCouponcodeBindRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            ticketCouponBind.setMsg(e.toString());
            CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "绑定优惠码", "POST", "AlibabaDamaiMzUserCouponcodeBindRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 获取票品优惠规则详情
     * @参数: [promotion_id 优惠id]
     */
    public static JSONObject getPromotionDetail(String promotion_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzPromotionDetailRequest req = new AlibabaDamaiMzPromotionDetailRequest();
        req.setPromotionId(promotion_id);
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzPromotionDetailResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_promotion_detail_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取票品优惠规则详情", "POST", "AlibabaDamaiMzPromotionDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取票品优惠规则详情", "POST", "AlibabaDamaiMzPromotionDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取票品优惠规则详情", "POST", "AlibabaDamaiMzPromotionDetailRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 根据手机号获取会员信息
     * @参数: [mobile_phone 手机号]
     */
    public static JSONObject getUserBaseInfoByPhone(String mobile_phone) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserBaseinfoGetRequest req = new AlibabaDamaiMzUserBaseinfoGetRequest();
        req.setMobilePhone(mobile_phone);

        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserBaseinfoGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_baseinfo_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "根据手机号获取会员信息", "POST", "AlibabaDamaiMzUserBaseinfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200")) {
                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "根据手机号获取会员信息", "POST", "AlibabaDamaiMzUserBaseinfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取票品优惠规则详情", "POST", "AlibabaDamaiMzPromotionDetailRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        TaobaoClient client = new DefaultTaobaoClient("http://gww.api.taobao.com/router/rest", Const.MZ_CLIENT_APPKEY, Const.MZ_CLIENT_SECRET);
        AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            AlibabaDamaiMzUserAssetRuleGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeMilesString();

            log.info(rsp.getBody());
            JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_rule_get_response").getJSONObject("result");

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            e.printStackTrace();
        }
    }
}
