package com.centit.ticket.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.dao.*;
import com.centit.ticket.feigin.JPushFeignClient;
import com.centit.ticket.po.*;
import com.centit.ticket.schedule.MZProjectTask;
import com.centit.ticket.utils.MZService;
import com.centit.ticket.utils.StringUtil;
import com.centit.ticket.webmgr.service.ProjectManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>演出管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class ProjectManageServiceImpl implements ProjectManageService {
    public static final Log log = LogFactory.getLog(ProjectManageService.class);

    @Resource
    private TicketClassDao ticketClassDao;
    @Resource
    private TicketProjectDao ticketProjectDao;
    @Resource
    private TicketEventDao ticketEventDao;
    @Resource
    private TicketEventPriceDao ticketEventPriceDao;
    @Resource
    private TicketProjectWatchingNoticeDao ticketProjectWatchingNoticeDao;
    @Resource
    private TicketVenueDao ticketVenueDao;
    @Resource
    private TicketClassPhotoDao ticketClassPhotoDao;
    @Resource
    private TicketProjectGuideDao ticketProjectGuideDao;
    @Resource
    private TicketProjectGoodsDao ticketProjectGoodsDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private TicketRecommendDao ticketRecommendDao;

    @Resource
    private MZProjectTask mzProjectTask;

    @Resource
    private JPushFeignClient jPushFeignClient;

    @Resource
    private ShoppingUserDao shoppingUserDao;

    @Resource
    private TicketProjectSponsorDao ticketProjectSponsorDao;

    /**
     * 查询演出分类
     */
    @Override
    public JSONObject queryTicketClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            List<TicketClass> classList = ticketClassDao.queryList(reqMap);
            bizDataJson.put("objList", classList);
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
     * 编辑演出分类
     */
    @Override
    public JSONObject editTicketClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketClass ticketClass= JSONObject.parseObject(reqJson.toJSONString(), TicketClass.class);
            ticketClassDao.update(ticketClass);

            TicketClassPhoto ticketClassPhoto = JSONObject.parseObject(reqJson.toJSONString(), TicketClassPhoto.class);
            ticketClassPhotoDao.delete(ticketClassPhoto);
            ticketClassPhotoDao.insert(ticketClassPhoto);
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
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            JSONArray objList = new JSONArray();
//            List<TicketEvent> eventList = ticketEventDao.queryClassEvents(reqMap);
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            JSONArray objList = new JSONArray();
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total", ticketEventDao.queryTotalCount(reqMap));
            List<TicketEvent> eventList = ticketEventDao.queryPageList(reqMap);
            for (TicketEvent ticketEvent : eventList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketEvent.getProjectId();  //演出项目id
                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    projectObj.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
                    projectObj.put("timeStr", str1 + "-" + str2);
                }
                //电子节目单
//                TicketProjectGuide ticketProjectGuide = new TicketProjectGuide();
//                ticketProjectGuide.setProjectId(projectId);
//                ticketProjectGuide = ticketProjectGuideDao.queryDetail(ticketProjectGuide);
//                projectObj.put("ticketProjectGuide",ticketProjectGuide==null?null:ticketProjectGuide.getPhotoId());
                reqMap.clear();
                reqMap.put("projectId",projectId);
                projectObj.put("ticketProjectGuides",ticketProjectGuideDao.queryList(reqMap));
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
            TicketProject ticketProject = new TicketProject();
            ticketProject.setProjectId(projectId);
            ticketProject = ticketProjectDao.queryDetail(ticketProject);
            bizDataJson.put("projectId", projectId);
            bizDataJson.put("projectName", ticketProject.getProjectName());
            bizDataJson.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
            bizDataJson.put("projectIntroduce", ticketProject.getProjectIntroduce());   //详情介绍
            bizDataJson.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座

            //查询观演须知
            TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
            ticketProjectWatchingNotice.setProjectId(projectId);
            ticketProjectWatchingNotice = ticketProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

            //查询该项目在售的所有的场次信息，按时间排序
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("projectId", projectId);
            List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
            //处理场次时间
            if (events.size() == 1) {
                bizDataJson.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
            } else if (events.size() > 1) {
                String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
                bizDataJson.put("timeStr", str1 + "-" + str2);
            }
            List<Integer> prices = new ArrayList<>();
            Set<String> venues = new HashSet<>();
            for (TicketEvent event : events) {
                //场次票档信息
                HashMap<String, Object> pMap = new HashMap<>();
                pMap.put("eventId", event.getEventId());
                pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                for (TicketEventPrice ticketEventPrice : priceList) {
                    int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                    ticketEventPrice.setPriceMoneyYuan(new BigDecimal((float) priceMoneyFen / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                    prices.add(priceMoneyFen);
                }
                event.setPriceList(priceList);
                //场馆
                venues.add(event.getVenueId());
            }
            //获取最低价格
            int minPrice = Collections.min(prices);
            bizDataJson.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            bizDataJson.put("events", events);
            //场馆信息
            List<TicketVenue> venueList = new ArrayList<>();
            for (String venueId : venues) {
                TicketVenue ticketVenue = new TicketVenue();
                ticketVenue.setVenueId(venueId);
                ticketVenue = ticketVenueDao.queryDetail(ticketVenue);
                venueList.add(ticketVenue);
            }

            bizDataJson.put("venueList", venueList);
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
     * 项目关联文创与积分商品列表
     */
    @Override
    public JSONObject projectGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");  //演出项目id
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("projectId", projectId);
            List<TicketProjectGoods> goodsList = ticketProjectGoodsDao.queryList(reqMap);
            JSONArray objList = new JSONArray();
            for(TicketProjectGoods ticketProjectGoods:goodsList){
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(ticketProjectGoods.getGoodsId());
                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                JSONObject goodsObj = new JSONObject();
                goodsObj.put("id",ticketProjectGoods.getId());
                goodsObj.put("goodsId",shoppingGoods.getId());
                goodsObj.put("photoId",shoppingGoods.getGoodsMainPhotoId());
                goodsObj.put("goodsName",shoppingGoods.getGoodsName());
                goodsObj.put("goodsType",ticketProjectGoods.getGoodsType());
                goodsObj.put("sn",ticketProjectGoods.getSn());
                objList.add(goodsObj);
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
     * 新增项目关联文创与积分商品
     */
    @Override
    public JSONObject addProjectGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketProjectGoods goods = JSONObject.parseObject(reqJson.toJSONString(), TicketProjectGoods.class);
            if(ticketProjectGoodsDao.queryDetail(goods)!=null){
                retMsg = "该商品已关联至当前演出，请勿重复添加！";
            }else{
                ticketProjectGoodsDao.insert(goods);
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
     * 删除项目关联文创与积分商品
     */
    @Override
    public JSONObject delProjectGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketProjectGoods goods = JSONObject.parseObject(reqJson.toJSONString(), TicketProjectGoods.class);
            ticketProjectGoodsDao.delete(goods);
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
     * 修改项目关联文创与积分商品
     */
    @Override
    public JSONObject editProjectGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketProjectGoods goods = JSONObject.parseObject(reqJson.toJSONString(), TicketProjectGoods.class);
            ticketProjectGoodsDao.update(goods);
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

//    /**
//     * 保存项目关联文创与积分商品列表
//     */
//    @Override
//    public JSONObject saveProjectGoodsList(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            String projectId = reqJson.getString("projectId");  //演出项目id
//            TicketProjectGoods ticketProjectGoods= new TicketProjectGoods();
//            ticketProjectGoods.setProjectId(projectId);
//            ticketProjectGoodsDao.delete(ticketProjectGoods);
//            JSONArray goodsArray = reqJson.getJSONArray("goodsList");
//            for(int i=0;i<goodsArray.size();i++){
//                TicketProjectGoods goods = JSONObject.parseObject(goodsArray.getJSONObject(i).toJSONString(), TicketProjectGoods.class);
//                ticketProjectGoodsDao.insert(goods);
//            }
//
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 设置演出项目电子节目单
     */
    @Override
    public JSONObject saveProjectGuide(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");
            JSONArray array = reqJson.getJSONArray("ticketProjectGuides");
            TicketProjectGuide ticketProjectGuide = new TicketProjectGuide();
            ticketProjectGuide.setProjectId(projectId);
            ticketProjectGuideDao.delete(ticketProjectGuide);
            for(int i=0;i<array.size();i++){
                JSONObject obj = array.getJSONObject(i);
                TicketProjectGuide guide = new TicketProjectGuide();
                guide.setProjectId(projectId);
                guide.setPhotoId(obj.getString("photoId"));
                ticketProjectGuideDao.insert(guide);
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
     * 查询已设置为推荐的演出列表
     */
    @Override
    public JSONObject queryRecommondProjectList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            JSONArray objList = new JSONArray();
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total", ticketRecommendDao.queryTotalCount(reqMap));
            List<TicketRecommend> recList = ticketRecommendDao.queryList(reqMap);
            for (TicketRecommend ticketRecommend : recList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketRecommend.getProjectId();  //演出项目id
                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                projectObj.put("id", ticketRecommend.getId());
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("sn", ticketRecommend.getSn());
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    projectObj.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
                    projectObj.put("timeStr", str1 + "-" + str2);
                }
//                //电子节目单
//                TicketProjectGuide ticketProjectGuide = new TicketProjectGuide();
//                ticketProjectGuide.setProjectId(projectId);
//                ticketProjectGuide = ticketProjectGuideDao.queryDetail(ticketProjectGuide);
//                projectObj.put("ticketProjectGuide",ticketProjectGuide==null?null:ticketProjectGuide.getPhotoId());
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
     * 新增推荐演出
     */
    @Override
    public JSONObject addRecProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRecommend ticketRecommend = JSONObject.parseObject(reqJson.toJSONString(), TicketRecommend.class);
            if(ticketRecommendDao.queryDetail(ticketRecommend)!=null){
                retMsg = "请勿重复添加！";
            }else{
                ticketRecommendDao.insert(ticketRecommend);
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
     * 删除推荐演出
     */
    @Override
    public JSONObject delRecProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRecommend ticketRecommend = JSONObject.parseObject(reqJson.toJSONString(), TicketRecommend.class);
            ticketRecommendDao.delete(ticketRecommend);
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
     * 修改推荐演出
     */
    @Override
    public JSONObject editRecProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRecommend ticketRecommend = JSONObject.parseObject(reqJson.toJSONString(), TicketRecommend.class);
            ticketRecommendDao.update(ticketRecommend);
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
     * 从麦座同步数据
     */
    @Override
    public JSONObject syncData(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            mzProjectTask.syncData();
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
     * 商品推送
     */
    @Override
    public JSONObject pushProjectMsg(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");  //演出项目id
            int pushType = reqJson.getInteger("pushType");  //1:全体；2：感兴趣的人
            String title = reqJson.getString("title");  //推送通知标题
            String message = reqJson.getString("message");  //推送通知内容

            if(pushType==1){
                reqJson.put("title",title);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("type",5);
                dataJson.put("title",title);
                dataJson.put("id",projectId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsgAll(reqJson);
            }else if(pushType==2){
                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);

                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",projectId);
                reqMap.put("cartType",5);
                List<ShoppingUser> users = shoppingUserDao.queryProjectUserList(reqMap);

                List<String> mobiles = new ArrayList<>();
//                for(ShoppingUser user:users){
//                    mobiles.add(user.getMobile());
//                }
                //暂时先写死几个手机号以便测试
                mobiles.add("13776407246");
                mobiles.add("13655174215");
                mobiles.add("13815897883");
                mobiles.add("18326162160");
                mobiles.add("13218479927");
                mobiles.add("17812301412");
                mobiles.add("13915940779");

                reqJson.put("mobiles",mobiles);
                reqJson.put("title",title);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("title",title);
                dataJson.put("type",5);
                dataJson.put("id",projectId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsg(reqJson);
            }else if(pushType==3){   //指定手机号的用户
                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);

                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",projectId);
                reqMap.put("cartType",5);

                String mobileStrs = reqJson.get("mobiles")==null?"":reqJson.getString("mobiles");
                String[] strs = mobileStrs.split(";");

                List<String> mobiles = new ArrayList<>();
                for(int i=0;i<strs.length;i++){
                    mobiles.add(strs[i]);
                }

                reqJson.put("mobiles",mobiles);
                reqJson.put("title",title);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("title",title);
                dataJson.put("type",5);
                dataJson.put("id",projectId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsg(reqJson);
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

//    /**
//     * 同步单个项目数据
//     */
//    @Override
//    public JSONObject syncProject(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            String project_id = reqJson.getString("projectId");
//            //查询项目详情
//            JSONObject detailObj = MZService.getProjectDetail(project_id);
//            if(null !=detailObj){
//                //项目基本信息
//                String project_name = detailObj.getString("project_name");   //项目名称
//                int project_seat_type = detailObj.getInteger("project_seat_type");   //项目座位类型；1=有座自助选座，2=无座
//                int project_sale_state = detailObj.getInteger("project_sale_state");   //项目销售状态；2=销售中；只返回在售项目
//                String first_class_id = detailObj.getString("first_class_id");   //一级项目分类id
//                String first_class_name = detailObj.getString("first_class_name");    //一级项目分类名称
//                String second_class_id = detailObj.get("second_class_id")==null?"":detailObj.getString("second_class_id");  //二级项目分类id；可空
//                String second_class_name = detailObj.get("second_class_name")==null?"":detailObj.getString("second_class_name");   //二级项目分类名称
//                String project_img_url = detailObj.get("project_img_url")==null?"":detailObj.getString("project_img_url");   //项目海报图片地址；可空
//                String project_round = detailObj.get("project_round")==null?"":detailObj.getString("project_round");    //项目轮次
//                String project_introduce = detailObj.getString("project_introduce");  //项目简介
//
//                TicketProject ticketProject = new TicketProject();
//                ticketProject.setProjectId(project_id);
//                ticketProject.setProjectName(project_name);
//                ticketProject.setProjectSeatType(project_seat_type);
//                ticketProject.setProjectSaleState(project_sale_state);
//                ticketProject.setFirstClassId(first_class_id);
//                ticketProject.setFirstClassName(first_class_name);
//                ticketProject.setSecondClassId(second_class_id);
//                ticketProject.setSecondClassName(second_class_name);
//                ticketProject.setProjectImgUrl(project_img_url);
//                ticketProject.setProjectRound(project_round);
//                ticketProject.setProjectIntroduce(project_introduce);
//                //观影须知
//                if(null !=detailObj.get("project_watching_notice")){
//                    JSONObject watchingNoticeObj = detailObj.getJSONObject("project_watching_notice");
//                    String show_length_tips = watchingNoticeObj.get("show_length_tips")==null?"":watchingNoticeObj.getString("show_length_tips");   //演出时长
//                    String entry_time_tips = watchingNoticeObj.get("entry_time_tips")==null?"":watchingNoticeObj.getString("entry_time_tips");   //入场时间
//                    String children_entry_tips = watchingNoticeObj.get("children_entry_tips")==null?"":watchingNoticeObj.getString("children_entry_tips");   //儿童入场提示
//                    String deposit_tips = watchingNoticeObj.get("deposit_tips")==null?"":watchingNoticeObj.getString("deposit_tips");    //寄存说明
//                    String prohibit_goods_tips = watchingNoticeObj.get("prohibit_goods_tips")==null?"":watchingNoticeObj.getString("prohibit_goods_tips");   //禁止携带的物品说明
//
//                    TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
//                    ticketProjectWatchingNotice.setProjectId(project_id);
//                    ticketProjectWatchingNotice.setShowLengthTips(show_length_tips);
//                    ticketProjectWatchingNotice.setEntryTimeTips(entry_time_tips);
//                    ticketProjectWatchingNotice.setChildrenEntryTips(children_entry_tips);
//                    ticketProjectWatchingNotice.setDepositTips(deposit_tips);
//                    ticketProjectWatchingNotice.setProhibitGoodsTips(prohibit_goods_tips);
//
//                    ticketProjectWatchingNoticeDao.delete(ticketProjectWatchingNotice);
//                    ticketProjectWatchingNoticeDao.insert(ticketProjectWatchingNotice);
//                }
//                //项目主办信息
//                if(null !=detailObj.get("sponsor_info_list")){
//                    try{
//                        JSONObject sponsor_info_list = detailObj.getJSONObject("sponsor_info_list");
//                        if(null !=sponsor_info_list.get("sponsor_info")){
//                            //先清除已有的项目主办方信息
//                            TicketProjectSponsor projectSponsor = new TicketProjectSponsor();
//                            projectSponsor.setProjectId(project_id);
//                            ticketProjectSponsorDao.delete(projectSponsor);
//
//                            JSONArray sponsorInfoArray = sponsor_info_list.getJSONArray("sponsor_info");
//                            for(int m=0;m<sponsorInfoArray.size();m++){
//                                JSONObject infoObj = sponsorInfoArray.getJSONObject(m);
//                                String sponsor_name = infoObj.getString("sponsor_name");  //主办方名称
//                                String sponsor_id = infoObj.getString("sponsor_id");   //主办方id
//                                TicketProjectSponsor ticketProjectSponsor = new TicketProjectSponsor();
//                                ticketProjectSponsor.setProjectId(project_id);
//                                ticketProjectSponsor.setSponsorId(sponsor_id);
//                                ticketProjectSponsor.setSponsorName(sponsor_name);
//                                ticketProjectSponsorDao.insert(ticketProjectSponsor);
//                            }
//                        }
//                    }catch(Exception e){
//                    }
//                }
//
//                ticketProjectDao.delete(ticketProject);
//                ticketProjectDao.insert(ticketProject);
//
//                //同步项目的场次数据
//                mzProjectTask.syncEventInfo(project_id);
//
//            }
//
//
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }
}
