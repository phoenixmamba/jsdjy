package com.centit.ticket.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.common.enums.Const;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MZService {
    private static final Logger log = LoggerFactory.getLogger(MZService.class);

    public static TaobaoClient getClient() {
        return new DefaultTaobaoClient(Const.MZ_CLIENT_URL, Const.MZ_CLIENT_APPKEY, Const.MZ_CLIENT_SECRET);
    }


    /**
     * @描述: 获取项目分类列表
     * @参数:
     */
    public static JSONObject getClassList() {

        TaobaoClient client = getClient();
        AlibabaDamaiMzProjectClassListRequest req = new AlibabaDamaiMzProjectClassListRequest();
//            req.setFirstClassId("19737107001");
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzProjectClassListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();
            log.info(rsp.getBody());
            try {
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_project_class_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取项目分类列表", "POST", "AlibabaDamaiMzProjectClassListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                    return resObj.getJSONObject("data_list");
                } else {
                    return null;
                }
            } catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目分类列表", "POST", "AlibabaDamaiMzProjectClassListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目分类列表", "POST", "AlibabaDamaiMzProjectClassListRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @描述: 获取项目列表
     * @参数:
     */
    public static JSONArray getProjectList() {
        JSONArray resArray = new JSONArray();
        try {
            TaobaoClient client = getClient();
            AlibabaDamaiMzProjectListRequest req = new AlibabaDamaiMzProjectListRequest();
            boolean isend = false;
            Long page = 1L;
            while (!isend) {
                req.setPage(page);
                req.setPageSize(100L);
                String reqtime = StringUtil.nowTimeString();
                try {
                    AlibabaDamaiMzProjectListResponse rsp = client.execute(req);
                    String rettime = StringUtil.nowTimeString();
                    log.info(rsp.getBody());
                    try {
                        JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_project_list_response").getJSONObject("result");
                        CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取项目列表", "POST", "AlibabaDamaiMzProjectListRequest",
                                reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                        if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                            JSONObject dataObj = resObj.getJSONObject("data");
                            Long total_page = dataObj.getLongValue("total_page");
                            JSONObject data_list = dataObj.getJSONObject("data_list");
                            resArray.addAll(data_list.getJSONArray("project_v_o"));
                            if (page == total_page) {
                                isend = true;
                            } else {
                                page = page + 1;
                            }
                        } else {
                            resArray = null;
                            break;
                        }
                    } catch (Exception e) {
                        CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目列表", "POST", "AlibabaDamaiMzProjectListRequest",
                                reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                        e.printStackTrace();
                        resArray = null;
                        break;
                    }
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目列表", "POST", "AlibabaDamaiMzProjectListRequest",
                            reqtime, req.getTextParams().toString(), rettime, e.toString());
                    e.printStackTrace();
                    return null;
                }
            }
            return resArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取项目详情
     * @参数:
     */
    public static JSONObject getProjectDetail(String project_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzProjectDetailRequest req = new AlibabaDamaiMzProjectDetailRequest();
        req.setProjectId(project_id);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzProjectDetailResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_project_detail_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取项目详情", "POST", "AlibabaDamaiMzProjectDetailResponse",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目详情", "POST", "AlibabaDamaiMzProjectDetailResponse",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取项目详情", "POST", "AlibabaDamaiMzProjectDetailResponse",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取场次信息
     * @参数:
     */
    public static JSONObject getProjectEvent(String project_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzEventListRequest req = new AlibabaDamaiMzEventListRequest();
        AlibabaDamaiMzEventListRequest.EventListParam obj1 = new AlibabaDamaiMzEventListRequest.EventListParam();
        obj1.setProjectId(project_id);
        req.setParam(obj1);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzEventListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_event_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取场次信息", "POST", "AlibabaDamaiMzEventListResponse",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data_list");
                } else {
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取场次信息", "POST", "AlibabaDamaiMzEventListResponse",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取场次信息", "POST", "AlibabaDamaiMzEventListResponse",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取场馆信息
     * @参数:
     */
    public static JSONObject getVenueInfo(String venue_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzVenueInfoGetRequest req = new AlibabaDamaiMzVenueInfoGetRequest();
        req.setVenueId(venue_id);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzVenueInfoGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_venue_info_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取场馆信息", "POST", "AlibabaDamaiMzVenueInfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取场馆信息", "POST", "AlibabaDamaiMzVenueInfoGetRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取场馆信息", "POST", "AlibabaDamaiMzVenueInfoGetRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
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
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressListResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_list_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址列表", "POST", "AlibabaDamaiMzUserAddressListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址列表", "POST", "AlibabaDamaiMzUserAddressListRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
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
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressDetailResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_detail_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return resObj.getJSONObject("data");
                } else {
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取会员收货地址详情", "POST", "AlibabaDamaiMzUserAddressDetailRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 设置默认收货地址
     * @参数: [userId 用户id]
     */
    public static Boolean defaultAddress(String mzUserId, String addressId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressDefaultRequest req = new AlibabaDamaiMzUserAddressDefaultRequest();
        req.setMzUserId(mzUserId);
        req.setAddressId(addressId);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressDefaultResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_default_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "设置默认收货地址", "POST", "AlibabaDamaiMzUserAddressDefaultRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "设置默认收货地址", "POST", "AlibabaDamaiMzUserAddressDefaultRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "设置默认收货地址", "POST", "AlibabaDamaiMzUserAddressDefaultRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 新增收货地址
     * @参数: [userId 用户id]
     */
    public static Boolean addAddress(String mzUserId, String receiver_name, String receiver_phone, String province_code, String city_code, String area_code, String address, String post_code) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressAddRequest req = new AlibabaDamaiMzUserAddressAddRequest();
        req.setMzUserId(mzUserId);
        req.setReceiverName(receiver_name);
        req.setReceiverPhone(receiver_phone);
        req.setProvinceCode(Long.parseLong(province_code));
        req.setCityCode(Long.parseLong(city_code));
        req.setAreaCode(Long.parseLong(area_code));
        req.setAddress(address);
        req.setPostCode(post_code);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressAddResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_add_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "新增收货地址", "POST", "AlibabaDamaiMzUserAddressAddRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "新增收货地址", "POST", "AlibabaDamaiMzUserAddressAddRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "新增收货地址", "POST", "AlibabaDamaiMzUserAddressAddRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 修改收货地址
     * @参数: [userId 用户id]
     */
    public static Boolean editAddress(String mzUserId, String address_id, String receiver_name, String receiver_phone, String province_code, String city_code, String area_code, String address, String post_code) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressModifyRequest req = new AlibabaDamaiMzUserAddressModifyRequest();
        req.setMzUserId(mzUserId);
        req.setAddressId(address_id);
        req.setReceiverName(receiver_name);
        req.setReceiverPhone(receiver_phone);
        req.setProvinceCode(Long.parseLong(province_code));
        req.setCityCode(Long.parseLong(city_code));
        req.setAreaCode(Long.parseLong(area_code));
        req.setAddress(address);
        req.setPostCode(post_code);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "修改收货地址", "POST", "AlibabaDamaiMzUserAddressModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "修改收货地址", "POST", "AlibabaDamaiMzUserAddressModifyRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "修改收货地址", "POST", "AlibabaDamaiMzUserAddressModifyRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 删除收货地址
     * @参数: [userId 用户id]
     */
    public static Boolean removeAddress(String mzUserId, String address_id) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressRemoveRequest req = new AlibabaDamaiMzUserAddressRemoveRequest();
        req.setMzUserId(mzUserId);
        req.setAddressId(address_id);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressRemoveResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_remove_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "删除收货地址", "POST", "AlibabaDamaiMzUserAddressRemoveRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {

                    return true;
                } else {
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "删除收货地址", "POST", "AlibabaDamaiMzUserAddressRemoveRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "删除收货地址", "POST", "AlibabaDamaiMzUserAddressRemoveRequest",
                    reqtime, req.getTextParams().toString(), rettime, e.toString());
            e.printStackTrace();
            return false;
        }
    }

//    /**
//     * @描述: 获取积分和余额免密限额信息
//     * @参数: [userId 用户id]
//     */
//    public static JSONObject getAssetRule() {
//
//        TaobaoClient client = getClient();
//        AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
//        String reqtime = StringUtil.nowTimeString();
//        try {
//            AlibabaDamaiMzUserAssetRuleGetResponse rsp = client.execute(req);
//            String rettime = StringUtil.nowTimeString();
//            log.info(rsp.getBody());
//            try {
//                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_rule_get_response").getJSONObject("result");
//                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
//                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
//                if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
//                    return resObj.getJSONObject("data");
//                } else {
//                    return null;
//                }
//            } catch (Exception e) {
//                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
//                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
//                e.printStackTrace();
//                return null;
//            }
//
//        } catch (Exception e) {
//            String rettime = StringUtil.nowTimeString();
//            CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_MZ, "获取积分和余额免密限额信息", "POST", "AlibabaDamaiMzUserAssetRuleGetRequest",
//                    reqtime, req.getTextParams().toString(), rettime, e.toString());
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static void main(String[] args) {
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "32838507", "f0ff6a087d9543f613616274b509ac60");
//        TaobaoClient client = getClient();
        AlibabaDamaiMzUserAddressAddRequest req = new AlibabaDamaiMzUserAddressAddRequest();
        req.setMzUserId("8729591");
        req.setReceiverName("崔建");
        req.setReceiverPhone("13776407246");
        req.setProvinceCode(440000L);
        req.setCityCode(441900L);
        req.setAreaCode(441901119L);
        req.setAddress("测试地址");
        req.setPostCode("523000");
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAddressAddResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_address_add_response").getJSONObject("result");
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_MZ, "新增收货地址", "POST", "AlibabaDamaiMzUserAddressAddRequest",
                        reqtime, req.getTextParams().toString(), rettime, rsp.getBody());
                System.out.println(resObj);
            }catch (Exception e) {

                e.printStackTrace();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
