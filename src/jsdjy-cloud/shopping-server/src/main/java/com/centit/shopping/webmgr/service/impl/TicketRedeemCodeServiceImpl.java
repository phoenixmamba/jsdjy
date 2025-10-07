package com.centit.shopping.webmgr.service.impl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.ExportExcel;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.TicketRedeemCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * <p>兑换码<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2022-07-18
 **/
@Transactional(rollbackFor = Exception.class)
@Service
public class TicketRedeemCodeServiceImpl implements TicketRedeemCodeService {
    public static final Log log = LogFactory.getLog(TicketRedeemCodeService.class);

    @Resource
    private TicketRedeemCodeDao ticketRedeemCodeDao;

    @Resource
    private TicketRedeemBatchDao ticketRedeemBatchDao;

    @Resource
    private TicketRedeemCompanyDao ticketRedeemCompanyDao;

    @Resource
    private TicketRedeemEventDao ticketRedeemEventDao;

    @Resource
    private TicketRedeemProjectDao ticketRedeemProjectDao;

    @Resource
    private TicketRedeemProjectWatchingNoticeDao ticketRedeemProjectWatchingNoticeDao;

    @Resource
    private TicketVenueDao ticketVenueDao;

    @Resource
    private TicketRedeemActivityDao ticketRedeemActivityDao;

    @Resource
    private TicketRedeemActivityBindDao ticketRedeemActivityBindDao;

    @Resource
    private TicketRedeemActivityProjectDao ticketRedeemActivityProjectDao;

    @Resource
    private TExportFileDao tExportFileDao;

    /**
     * 查询兑换码创建批次列表
     */
    @Override
    public JSONObject queryBatchList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemBatchDao.queryTotalCount(reqMap));
            List<TicketRedeemBatch> objList = ticketRedeemBatchDao.queryList(reqMap);
            bizDataJson.put("objList", objList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 创建指定数量的兑换码
     */
    @Override
    public JSONObject createCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemBatch ticketRedeemBatch = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemBatch.class);
            int codeCount = ticketRedeemBatch.getCodeCount();  //创建优惠码数量
            String codePrefix = ticketRedeemBatch.getCodePrefix();   //优惠码前缀
            ticketRedeemBatchDao.insert(ticketRedeemBatch);

            //查询指定前缀优惠码已创建的最大编号
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("prefix", codePrefix);
            TicketRedeemCode largestCode = ticketRedeemCodeDao.queryLargestCode(reqMap);

            int startNumber = largestCode == null ? 0 : largestCode.getNumber();
            String batchId = ticketRedeemBatch.getId();
            for (int i = 1; i <= codeCount; i++) {
                TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
                ticketRedeemCode.setBatchId(batchId);
                ticketRedeemCode.setPrefix(codePrefix);
                ticketRedeemCode.setNumber(startNumber + i);
                DecimalFormat decimalFormat = new DecimalFormat("00000000");  //8位数字，前面用0补足
                String numFormat = decimalFormat.format(ticketRedeemCode.getNumber());
                ticketRedeemCode.setCode(codePrefix + numFormat);
                String pwd = StringUtil.randomCode(8);
                ticketRedeemCode.setPwd(pwd);
                ticketRedeemCodeDao.insert(ticketRedeemCode);
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 按批次直接删除兑换码
     */
    @Override
    public JSONObject deleteBatch(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String batchId = reqJson.getString("batchId");
            //查询该批次下是否有已被绑定的兑换码
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("isDelete", "0");
            reqMap.put("batchId", batchId);
            reqMap.put("isBind", 1);
            List<TicketRedeemCode> objList = ticketRedeemCodeDao.queryList(reqMap);
            if (objList.isEmpty()) {
                TicketRedeemBatch ticketRedeemBatch = new TicketRedeemBatch();
                ticketRedeemBatch.setId(batchId);
                ticketRedeemBatch.setIsDelete("1");
                ticketRedeemBatchDao.update(ticketRedeemBatch);

                TicketRedeemCode code = new TicketRedeemCode();
                code.setBatchId(batchId);
                ticketRedeemCodeDao.deleteByBatchId(code);

                retCode = "0";
                retMsg = "操作成功！";
            } else {
                retMsg = "当前批次下存在已被绑定的兑换码，无法直接删除！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询已创建的发卡单位列表
     */
    @Override
    public JSONObject queryCompanyList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemCompanyDao.queryTotalCount(reqMap));
            List<TicketRedeemCompany> objList = ticketRedeemCompanyDao.queryList(reqMap);
            bizDataJson.put("objList", objList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 创建单位
     */
    @Override
    public JSONObject createCompany(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemCompany ticketRedeemCompany = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemCompany.class);
            ticketRedeemCompanyDao.insert(ticketRedeemCompany);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 编辑单位
     */
    @Override
    public JSONObject editCompany(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemCompany ticketRedeemCompany = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemCompany.class);
            ticketRedeemCompanyDao.update(ticketRedeemCompany);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 删除单位
     */
    @Override
    public JSONObject delCompany(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemCompany ticketRedeemCompany = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemCompany.class);
            ticketRedeemCompany.setIsDelete("1");
            ticketRedeemCompanyDao.update(ticketRedeemCompany);

            // 处理该单位下的兑换码活动
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("companyId", ticketRedeemCompany.getId());
            List<TicketRedeemActivity> activityList = ticketRedeemActivityDao.queryList(reqMap);
            for (TicketRedeemActivity ticketRedeemActivity : activityList) {
                ticketRedeemActivity.setIsDelete("1");
                ticketRedeemActivityDao.update(ticketRedeemActivity);
                HashMap<String, Object> bindMap = new HashMap<>();
                bindMap.put("activityId", ticketRedeemActivity.getId());
                ticketRedeemCodeDao.deleteCodeByActivity(bindMap);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询演出列表
     */
    @Override
    public JSONObject queryProjectList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            if (null != reqJson.get("pageNo")) {
                int pageNo = reqJson.getInteger("pageNo");
                int pageSize = reqJson.getInteger("pageSize");
                reqMap.put("startRow", (pageNo - 1) * pageSize);
                reqMap.put("pageSize", pageSize);
            }

            JSONArray objList = new JSONArray();
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemEventDao.queryTotalCount(reqMap));
            List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryPageList(reqMap);
            for (TicketRedeemEvent ticketEvent : eventList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketEvent.getProjectId();  //演出项目id
                //查询演出详情
                TicketRedeemProject ticketProject = new TicketRedeemProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketRedeemProjectDao.queryDetail(ticketProject);
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                projectObj.put("projectSaleState", ticketProject.getProjectSaleState());     //项目销售状态；1=待销售，2=销售中，3=销售结束
                projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                projectObj.put("saleStartTime", ticketProject.getSaleStartTime());   //开始销售时间
                projectObj.put("saleEndTime", ticketProject.getSaleEndTime());   //截止销售时间
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketRedeemEvent> events = ticketRedeemEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    projectObj.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
                    projectObj.put("timeStr", str1 + "-" + str2);
                }
                objList.add(projectObj);
            }
            bizDataJson.put("objList", objList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 项目详情
     */
    @Override
    public JSONObject projectDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");  //演出项目id
            //查询演出详情
            TicketRedeemProject ticketProject = new TicketRedeemProject();
            ticketProject.setProjectId(projectId);
            ticketProject = ticketRedeemProjectDao.queryDetail(ticketProject);
            bizDataJson.put("projectId", projectId);
            bizDataJson.put("projectName", ticketProject.getProjectName());
            bizDataJson.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
            bizDataJson.put("projectIntroduce", ticketProject.getProjectIntroduce());   //详情介绍
            bizDataJson.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
            bizDataJson.put("projectSaleState", ticketProject.getProjectSaleState());     //项目销售状态；1=待销售，2=销售中，3=销售结束
            bizDataJson.put("isBindall", ticketProject.getIsBindall());     //
            bizDataJson.put("saleStartTime", ticketProject.getSaleStartTime());
            bizDataJson.put("saleEndTime", ticketProject.getSaleEndTime());
            bizDataJson.put("firstClassId", ticketProject.getFirstClassId());
            bizDataJson.put("firstClassName", ticketProject.getFirstClassName());
            bizDataJson.put("writeOffCount", ticketProject.getWriteOffCount());
            //查询观演须知
            TicketRedeemProjectWatchingNotice ticketProjectWatchingNotice = new TicketRedeemProjectWatchingNotice();
            ticketProjectWatchingNotice.setProjectId(projectId);
            ticketProjectWatchingNotice = ticketRedeemProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

            //查询该项目在售的所有的场次信息，按时间排序
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("projectId", projectId);
            reqMap.put("isDelete", "0");
            List<TicketRedeemEvent> events = ticketRedeemEventDao.queryProjectEvents(reqMap);
            //处理场次时间
            if (events.size() == 1) {
                bizDataJson.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
            } else if (events.size() > 1) {
                String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
                bizDataJson.put("timeStr", str1 + "-" + str2);
            }
            List<Integer> prices = new ArrayList<>();
            //场馆信息
            List<TicketVenue> venueList = new ArrayList<>();
            for (TicketRedeemEvent event : events) {
//                //场次票档信息
//                HashMap<String, Object> pMap = new HashMap<>();
//                pMap.put("eventId", event.getEventId());
//                pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
//                List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
//                for (TicketEventPrice ticketEventPrice : priceList) {
//                    int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
//                    ticketEventPrice.setPriceMoneyYuan(new BigDecimal((float) priceMoneyFen / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
//                    prices.add(priceMoneyFen);
//                }
//                event.setPriceList(priceList);
                TicketVenue ticketVenue = new TicketVenue();
                ticketVenue.setVenueId(event.getVenueId());
                ticketVenue = ticketVenueDao.queryDetail(ticketVenue);
                venueList.add(ticketVenue);

                event.setVenueName(ticketVenue.getVenueName());
            }
            bizDataJson.put("events", events);
            bizDataJson.put("venueList", venueList);

            //项目绑定优惠码活动信息
            if (ticketProject.getIsBindall().equals("1")) {
                reqMap = new HashMap<>();
                reqMap.put("projectId", projectId);
                List<TicketRedeemActivityProject> activityProjects = ticketRedeemActivityProjectDao.queryList(reqMap);
                bizDataJson.put("bindActivitys", activityProjects);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取场馆列表
     */
    @Override
    public JSONObject queryVenueList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);

            List<TicketVenue> objList = ticketVenueDao.queryList(reqMap);
            bizDataJson.put("objList", objList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 新增项目
     */
    @Override
    public JSONObject addProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //项目信息
            TicketRedeemProject ticketRedeemProject = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemProject.class);
            ticketRedeemProjectDao.insert(ticketRedeemProject);

            //演出须知
            String projectId = ticketRedeemProject.getProjectId();
            TicketRedeemProjectWatchingNotice ticketRedeemProjectWatchingNotice = JSONObject.parseObject(reqJson.getJSONObject("watchingNotice").toJSONString(), TicketRedeemProjectWatchingNotice.class);
            ticketRedeemProjectWatchingNotice.setProjectId(projectId);
            ticketRedeemProjectWatchingNoticeDao.insert(ticketRedeemProjectWatchingNotice);

            //场次信息
            JSONArray eventArray = reqJson.getJSONArray("events");
            List<TicketRedeemEvent> eventList = new ArrayList<>();
            for (int i = 0; i < eventArray.size(); i++) {
                JSONObject obj = eventArray.getJSONObject(i);
                TicketRedeemEvent ticketRedeemEvent = JSONObject.parseObject(obj.toJSONString(), TicketRedeemEvent.class);
                ticketRedeemEvent.setProjectId(projectId);
                ticketRedeemEventDao.insert(ticketRedeemEvent);

                eventList.add(ticketRedeemEvent);
            }

            //处理演出项目关联兑换码活动
            List<TicketRedeemActivity> activities = new ArrayList<>();
            if (ticketRedeemProject.getIsBindall().equals("0")) {   //默认关联所有的兑换码活动
                HashMap<String, Object> reqMap = new HashMap<>();
                activities = ticketRedeemActivityDao.queryList(reqMap);
                for (TicketRedeemEvent event : eventList) {
                    for (TicketRedeemActivity ticketRedeemActivity : activities) {
                        if (ticketRedeemActivity.getIsBindall().equals("0")) {
                            TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                            ticketRedeemActivityProject.setProjectId(projectId);
                            ticketRedeemActivityProject.setEventId(event.getEventId());
                            ticketRedeemActivityProject.setActivityId(ticketRedeemActivity.getId());
                            ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                        }
                    }

                }
            } else {
                JSONArray actArray = reqJson.getJSONArray("bindActivitys");
                for (TicketRedeemEvent event : eventList) {
                    for (int i = 0; i < actArray.size(); i++) {
                        TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                        ticketRedeemActivityProject.setProjectId(projectId);
                        ticketRedeemActivityProject.setEventId(event.getEventId());
                        JSONObject obj = actArray.getJSONObject(i);
                        ticketRedeemActivityProject.setActivityId(obj.getString("activityId"));
                        ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                    }
                }

            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 编辑项目
     */
    @Override
    public JSONObject editProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //项目信息
            TicketRedeemProject ticketRedeemProject = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemProject.class);
            TicketRedeemProject oldTicketRedeemProject = ticketRedeemProjectDao.queryDetail(ticketRedeemProject);
            ticketRedeemProjectDao.update(ticketRedeemProject);
            String prijectId = ticketRedeemProject.getProjectId();
            //演出须知
            if (reqJson.get("watchingNotice") != null) {
                TicketRedeemProjectWatchingNotice ticketRedeemProjectWatchingNotice = JSONObject.parseObject(reqJson.getJSONObject("watchingNotice").toJSONString(), TicketRedeemProjectWatchingNotice.class);
                ticketRedeemProjectWatchingNotice.setProjectId(prijectId);
                ticketRedeemProjectWatchingNoticeDao.update(ticketRedeemProjectWatchingNotice);
            }
            //场次信息
            List<TicketRedeemEvent> eventList = new ArrayList<>();
            if (reqJson.get("events") != null) {
                JSONArray eventArray = reqJson.getJSONArray("events");
                for (int i = 0; i < eventArray.size(); i++) {
                    JSONObject obj = eventArray.getJSONObject(i);
                    TicketRedeemEvent ticketRedeemEvent = JSONObject.parseObject(obj.toJSONString(), TicketRedeemEvent.class);
                    eventList.add(ticketRedeemEvent);
                    if (StringUtil.isNotNull(ticketRedeemEvent.getEventId())) {
                        ticketRedeemEventDao.update(ticketRedeemEvent);
                    } else {
                        ticketRedeemEvent.setProjectId(prijectId);
                        ticketRedeemEventDao.insert(ticketRedeemEvent);
                    }
                }
            }

            //处理演出项目关联兑换码活动
            List<TicketRedeemActivity> activities = new ArrayList<>();
            if (ticketRedeemProject.getIsBindall().equals("0")) {   //关联所有的兑换码活动
                if (oldTicketRedeemProject.getIsBindall().equals("1")) {
                    //删除所有旧的绑定
                    TicketRedeemActivityProject activityProject = new TicketRedeemActivityProject();
                    activityProject.setProjectId(prijectId);
                    ticketRedeemActivityProjectDao.deleteByProject(activityProject);

                    HashMap<String, Object> reqMap = new HashMap<>();
                    activities = ticketRedeemActivityDao.queryList(reqMap);
                    for (TicketRedeemEvent event : eventList) {
                        for (TicketRedeemActivity ticketRedeemActivity : activities) {
                            if (ticketRedeemActivity.getIsBindall().equals("0")) {
                                TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                                ticketRedeemActivityProject.setProjectId(prijectId);
                                ticketRedeemActivityProject.setEventId(event.getEventId());
                                ticketRedeemActivityProject.setActivityId(ticketRedeemActivity.getId());
                                ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                            }

                        }

                    }
                }

            } else {
                //删除所有旧的绑定
                TicketRedeemActivityProject activityProject = new TicketRedeemActivityProject();
                activityProject.setProjectId(prijectId);
                ticketRedeemActivityProjectDao.deleteByProject(activityProject);

                JSONArray actArray = reqJson.getJSONArray("bindActivitys");
                for (TicketRedeemEvent event : eventList) {
                    for (int i = 0; i < actArray.size(); i++) {
                        TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                        ticketRedeemActivityProject.setProjectId(prijectId);
                        ticketRedeemActivityProject.setEventId(event.getEventId());
                        JSONObject obj = actArray.getJSONObject(i);
                        ticketRedeemActivityProject.setActivityId(obj.getString("activityId"));
                        ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                    }
                }

            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 删除项目
     */
    @Override
    public JSONObject delProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemProject ticketRedeemProject = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemProject.class);
            ticketRedeemProject.setIsDelete("1");
            ticketRedeemProjectDao.update(ticketRedeemProject);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取活动可用优惠码的起始编码
     */
    @Override
    public JSONObject getStartCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String codePrefix = reqJson.getString("codePrefix");
            //查询已被绑定的兑换码的最大编码
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("prefix", codePrefix);
            reqMap.put("isBind", 1);
            reqMap.put("isDelete", "0");
            TicketRedeemCode largestCode = ticketRedeemCodeDao.queryLargestCode(reqMap);

            //查询未被绑定的兑换码的起始编码（由于该编码和largestCode中间可能存在被删除的码，因此不能简单的认为该编码=largestCode+1）
            reqMap.put("startNum", largestCode.getNumber() + 1);
            reqMap.put("isBind", 0);
            TicketRedeemCode smallstCode = ticketRedeemCodeDao.querySmallestCode(reqMap);
            bizDataJson.put("startCode", smallstCode);
            bizDataJson.put("freeAmount", ticketRedeemCodeDao.queryFreeCodeCount(reqMap));

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 创建活动
     */
    @Override
    public JSONObject createActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //先判断用户选择绑定的编码是否可用
            int codeAmount = reqJson.getInteger("codeAmount");  //兑换码数量
            String startCode = reqJson.getString("startCode");  //起始码
            TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
            ticketRedeemCode.setCode(startCode);
            ticketRedeemCode = ticketRedeemCodeDao.queryDetail(ticketRedeemCode);
            //判断这批编码中是否存在不可用的编码（已被绑定或已被删除）
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("startNum", ticketRedeemCode.getNumber());
            reqMap.put("endNum", ticketRedeemCode.getNumber() + codeAmount);
            reqMap.put("isBind", 0);
            if (ticketRedeemCodeDao.queryFreeCodeCount(reqMap) < codeAmount) {
                retMsg = "剩余可用兑换码数量不足，或您选择的该批兑换码中存在不可用的兑换码（已被绑定或删除），请重新选择起始码或者联系开发人员！";
            } else {
                TicketRedeemActivity ticketRedeemActivity = JSONObject.parseObject(reqJson.getJSONObject("activity").toJSONString(), TicketRedeemActivity.class);
                ticketRedeemActivityDao.insert(ticketRedeemActivity);
                String activityId = ticketRedeemActivity.getId();
                //绑码记录
                TicketRedeemActivityBind ticketRedeemActivityBind = new TicketRedeemActivityBind();
                ticketRedeemActivityBind.setActivityId(activityId);
                ticketRedeemActivityBind.setAmount(codeAmount);
                ticketRedeemActivityBind.setStartCode(startCode);
                ticketRedeemActivityBind.setCreateUser(ticketRedeemActivity.getCreateUser());
                ticketRedeemActivityBindDao.insert(ticketRedeemActivityBind);
                //将兑换码与活动绑定
                HashMap<String, Object> bindMap = new HashMap<>();
                bindMap.put("startNum", ticketRedeemCode.getNumber());
                bindMap.put("endNum", ticketRedeemCode.getNumber() + codeAmount);
                bindMap.put("activityId", activityId);
                bindMap.put("bindId", ticketRedeemActivityBind.getId());
                ticketRedeemCodeDao.bindCode(bindMap);


                //处理活动与项目关联
                if (ticketRedeemActivity.getIsBindall().equals("0")) {   //默认关联所有的项目
//                    List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryPageList(new HashMap<>());
//
//                    for (TicketRedeemEvent event : eventList) {
//                        TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
//                        ticketRedeemActivityProject.setProjectId(event.getProjectId());
//                        ticketRedeemActivityProject.setEventId(event.getEventId());
//                        ticketRedeemActivityProject.setActivityId(activityId);
//                        ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
//                    }
                    List<TicketRedeemProject> projectList = ticketRedeemProjectDao.queryList(new HashMap<>());

                    for (TicketRedeemProject project : projectList) {
                        if (project.getIsBindall().equals("0")) {
                            reqMap.clear();
                            reqMap.put("projectId", project.getProjectId());
                            List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryProjectEvents(reqMap);
                            for (TicketRedeemEvent event : eventList) {
                                TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                                ticketRedeemActivityProject.setProjectId(event.getProjectId());
                                ticketRedeemActivityProject.setEventId(event.getEventId());
                                ticketRedeemActivityProject.setActivityId(activityId);
                                ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                            }
                        }
                    }
                } else {
                    JSONArray projArray = reqJson.getJSONArray("bindProjects");
                    for (int i = 0; i < projArray.size(); i++) {
                        JSONObject obj = projArray.getJSONObject(i);
                        String projectId = obj.getString("projectId");
                        HashMap<String, Object> projMap = new HashMap<>();
                        projMap.put("projectId", projectId);
                        List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryProjectEvents(projMap);
                        for (TicketRedeemEvent event : eventList) {
                            TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                            ticketRedeemActivityProject.setProjectId(projectId);
                            ticketRedeemActivityProject.setEventId(event.getEventId());
                            ticketRedeemActivityProject.setActivityId(activityId);
                            ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                        }
                    }
                }
                retCode = "0";
                retMsg = "操作成功！";
            }


        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询兑换码活动列表
     */
    @Override
    public JSONObject queryActivityList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            if (null != reqJson.get("pageNo")) {
                int pageNo = reqJson.getInteger("pageNo");
                int pageSize = reqJson.getInteger("pageSize");
                reqMap.put("startRow", (pageNo - 1) * pageSize);
                reqMap.put("pageSize", pageSize);
            }
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemActivityDao.queryTotalCount(reqMap));
            List<TicketRedeemActivity> activityList = ticketRedeemActivityDao.queryList(reqMap);

            bizDataJson.put("objList", activityList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取兑换码活动详情
     */
    @Override
    public JSONObject queryActivityDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String activityId = reqJson.getString("activityId");
            TicketRedeemActivity ticketRedeemActivity = new TicketRedeemActivity();
            ticketRedeemActivity.setId(activityId);
            ticketRedeemActivity = ticketRedeemActivityDao.queryDetail(ticketRedeemActivity);
            bizDataJson.put("activity", ticketRedeemActivity);
            if (ticketRedeemActivity.getIsBindall().equals("1")) {
                HashMap<String, Object> projMap = new HashMap<>();
                projMap.put("activityId", activityId);
                List<TicketRedeemActivityProject> projectList = ticketRedeemActivityProjectDao.queryList(projMap);
                bizDataJson.put("bindProjects", projectList);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 编辑活动信息
     */
    @Override
    public JSONObject editActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemActivity ticketRedeemActivity = JSONObject.parseObject(reqJson.getJSONObject("activity").toJSONString(), TicketRedeemActivity.class);
            TicketRedeemActivity oldActivity = ticketRedeemActivityDao.queryDetail(ticketRedeemActivity);
            ticketRedeemActivityDao.update(ticketRedeemActivity);
            String activityId = ticketRedeemActivity.getId();

            //处理活动与项目关联
            if (ticketRedeemActivity.getIsBindall().equals("0")) {   //默认关联所有的项目
                if (oldActivity.getIsBindall().equals("1")) {
                    //删除该活动之前已绑定的项目
                    TicketRedeemActivityProject ap = new TicketRedeemActivityProject();
                    ap.setActivityId(ticketRedeemActivity.getId());
                    ticketRedeemActivityProjectDao.deleteByActivity(ap);

//                    List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryPageList(new HashMap<>());
//                    for (TicketRedeemEvent event : eventList) {
//                        TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
//                        ticketRedeemActivityProject.setProjectId(event.getProjectId());
//                        ticketRedeemActivityProject.setEventId(event.getEventId());
//                        ticketRedeemActivityProject.setActivityId(activityId);
//                        ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
//                    }

                    List<TicketRedeemProject> projectList = ticketRedeemProjectDao.queryList(new HashMap<>());
                    HashMap<String, Object> reqMap = new HashMap<>();
                    for (TicketRedeemProject project : projectList) {
                        if (project.getIsBindall().equals("0")) {
                            reqMap.clear();
                            reqMap.put("projectId", project.getProjectId());
                            List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryProjectEvents(reqMap);
                            for (TicketRedeemEvent event : eventList) {
                                TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                                ticketRedeemActivityProject.setProjectId(event.getProjectId());
                                ticketRedeemActivityProject.setEventId(event.getEventId());
                                ticketRedeemActivityProject.setActivityId(activityId);
                                ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                            }
                        }
                    }
                }

            } else {
                //删除该活动之前已绑定的项目
                TicketRedeemActivityProject ap = new TicketRedeemActivityProject();
                ap.setActivityId(ticketRedeemActivity.getId());
                ticketRedeemActivityProjectDao.deleteByActivity(ap);

                JSONArray projArray = reqJson.getJSONArray("bindProjects");
                for (int i = 0; i < projArray.size(); i++) {
                    JSONObject obj = projArray.getJSONObject(i);
                    String projectId = obj.getString("projectId");
                    HashMap<String, Object> projMap = new HashMap<>();
                    projMap.put("projectId", projectId);
                    List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryProjectEvents(projMap);
                    for (TicketRedeemEvent event : eventList) {
                        TicketRedeemActivityProject ticketRedeemActivityProject = new TicketRedeemActivityProject();
                        ticketRedeemActivityProject.setProjectId(projectId);
                        ticketRedeemActivityProject.setEventId(event.getEventId());
                        ticketRedeemActivityProject.setActivityId(activityId);
                        ticketRedeemActivityProjectDao.insert(ticketRedeemActivityProject);
                    }
                }
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询活动绑卡记录
     */
    @Override
    public JSONObject queryActivityBindList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            if (null != reqJson.get("pageNo")) {
                int pageNo = reqJson.getInteger("pageNo");
                int pageSize = reqJson.getInteger("pageSize");
                reqMap.put("startRow", (pageNo - 1) * pageSize);
                reqMap.put("pageSize", pageSize);
            }
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemActivityBindDao.queryTotalCount(reqMap));
            List<TicketRedeemActivityBind> bindList = ticketRedeemActivityBindDao.queryList(reqMap);

            bizDataJson.put("objList", bindList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 按绑定记录删除兑换码
     */
    @Override
    public JSONObject delCodeByBindId(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String bindId = reqJson.getString("bindId");

            TicketRedeemActivityBind ticketRedeemActivityBind = new TicketRedeemActivityBind();
            ticketRedeemActivityBind.setId(bindId);
            ticketRedeemActivityBind.setIsDelete("1");
            ticketRedeemActivityBindDao.update(ticketRedeemActivityBind);

            HashMap<String, Object> bindMap = new HashMap<>();
            bindMap.put("bindId", bindId);
            ticketRedeemCodeDao.deleteCodeByBindId(bindMap);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 活动追加绑卡
     */
    @Override
    public JSONObject acticityAddCodes(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //先判断用户选择绑定的编码是否可用
            String activityId = reqJson.getString("activityId");  //活动id
            String createUser = reqJson.getString("createUser");  //创建人
            int codeAmount = reqJson.getInteger("codeAmount");  //兑换码数量
            String startCode = reqJson.getString("startCode");  //起始码
            TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
            ticketRedeemCode.setCode(startCode);
            ticketRedeemCode = ticketRedeemCodeDao.queryDetail(ticketRedeemCode);
            //判断这批编码中是否存在不可用的编码（已被绑定或已被删除）
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("startNum", ticketRedeemCode.getNumber());
            reqMap.put("endNum", ticketRedeemCode.getNumber() + codeAmount);
            reqMap.put("isBind", 0);
            if (ticketRedeemCodeDao.queryFreeCodeCount(reqMap) < codeAmount) {
                retMsg = "您选择的该批兑换码中存在不可用的兑换码（已被绑定或删除），请重新选择起始码或者联系开发人员！";
            } else {//绑码记录
                TicketRedeemActivityBind ticketRedeemActivityBind = new TicketRedeemActivityBind();
                ticketRedeemActivityBind.setActivityId(activityId);
                ticketRedeemActivityBind.setAmount(codeAmount);
                ticketRedeemActivityBind.setStartCode(startCode);
                ticketRedeemActivityBind.setCreateUser(createUser);
                ticketRedeemActivityBindDao.insert(ticketRedeemActivityBind);
                //将兑换码与活动绑定
                HashMap<String, Object> bindMap = new HashMap<>();
                bindMap.put("startNum", ticketRedeemCode.getNumber());
                bindMap.put("endNum", ticketRedeemCode.getNumber() + codeAmount);
                bindMap.put("activityId", activityId);
                bindMap.put("bindId", ticketRedeemActivityBind.getId());
                ticketRedeemCodeDao.bindCode(bindMap);
                retCode = "0";
                retMsg = "操作成功！";
            }


        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 上/下架活动
     */
    @Override
    public JSONObject pubActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemActivity ticketRedeemActivity = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemActivity.class);
            ticketRedeemActivityDao.update(ticketRedeemActivity);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 删除活动
     */
    @Override
    public JSONObject delActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRedeemActivity ticketRedeemActivity = JSONObject.parseObject(reqJson.toJSONString(), TicketRedeemActivity.class);
            ticketRedeemActivity.setIsDelete("1");
            ticketRedeemActivityDao.update(ticketRedeemActivity);
            HashMap<String, Object> bindMap = new HashMap<>();
            bindMap.put("activityId", ticketRedeemActivity.getId());
            ticketRedeemCodeDao.deleteCodeByActivity(bindMap);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询兑换码列表
     */
    @Override
    public JSONObject queryCodePageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemCodeDao.queryTotalCount(reqMap));
            List<TicketRedeemCode> objList = ticketRedeemCodeDao.queryList(reqMap);
            for (TicketRedeemCode code : objList) {
                code.setPwd(null);
            }
            bizDataJson.put("objList", objList);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 导出兑换码文件
     */
    @Override
    public JSONObject exportCodeFile(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("兑换码");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                    reqMap.put("isDelete", "0");
                    List<TicketRedeemCode> objList = ticketRedeemCodeDao.queryList(reqMap);
                    String sumStr = "兑换码";
                    // 导出表的标题
                    String title = sumStr;
                    // 导出表的列名
                    String[] rowsName = new String[]{"兑换码", "前缀", "密码", "用户手机号", "所属单位", "兑换项目", "兑换场次", "兑换时间", "核销时间", "所属兑换活动", "创建时间", "观影人姓名"
                            , "观影人手机号", "观影人证件号"};
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for (TicketRedeemCode ticketRedeemCode : objList) {
                        Object[] obj = new Object[14];
                        obj[0] = ticketRedeemCode.getCode();
                        obj[1] = ticketRedeemCode.getPrefix();
                        obj[2] = ticketRedeemCode.getPwd();
                        obj[3] = ticketRedeemCode.getExchangeMobile();
                        obj[4] = ticketRedeemCode.getCompanyName();
                        obj[5] = ticketRedeemCode.getProjectName();
                        obj[6] = ticketRedeemCode.getEventName();
                        obj[7] = ticketRedeemCode.getExchangeTime();
                        obj[8] = ticketRedeemCode.getWriteoffTime();
                        obj[9] = ticketRedeemCode.getActivityId();
                        obj[10] = ticketRedeemCode.getCreateTime();
                        obj[11] = ticketRedeemCode.getWatchingUser();
                        obj[12] = ticketRedeemCode.getWatchingMobile();
                        obj[13] = ticketRedeemCode.getWatchingCard();

                        dataList.add(obj);
                    }

                    String fileName = fileId + ".xls";
                    ShoppingSysconfig config = CommonUtil.getSysConfig();
                    String uploadFilePath = config.getUploadFilePath();
                    File file = new File(uploadFilePath + File.separator + "exportFile" +
                            File.separator + fileName);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        ExportExcel ex = new ExportExcel(title, rowsName, dataList);
                        ex.export(out);

                        tExportFile.setFileName(fileName);
                        tExportFile.setFinishTime(StringUtil.nowTimeString());
                        tExportFile.setTaskStatus(1);  //已完成

                        tExportFileDao.update(tExportFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tExportFile.setTaskStatus(-1);
                        tExportFileDao.update(tExportFile);
                    }
                }
            });
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 批量删除兑换码
     */
    @Override
    public JSONObject delCodes(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String codeStr = reqJson.getString("codeStr");
            String[] codes = codeStr.split(",");
            List<String> codeList = new ArrayList<>();
            for (int i = 0; i < codes.length; i++) {
                codeList.add(codes[i]);
            }
            HashMap<String, Object> delMap = new HashMap<>();
            delMap.put("codeList", codeList);
            ticketRedeemCodeDao.deleteCodes(delMap);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

}
