package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.PlatformMonitorDao;
import com.centit.shopping.po.PlatformMonitorInfo;
import com.centit.shopping.utils.BetweenDateUtils;
import com.centit.shopping.webmgr.service.PlatformMonitorService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlatformMonitorServiceImpl implements PlatformMonitorService {
    public static final Log LOG = LogFactory.getLog(PlatformMonitorService.class);

    @Resource
    private PlatformMonitorDao platformMonitorDao;

    @Override
    public JSONObject queryRegisterUserStatistics(JSONObject req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap reqMap = JSON.parseObject(req.toJSONString(), HashMap.class);
            if(req.getString("searchType").equals("month")){
                String end = req.getString("end");
                reqMap.put("end",end+"-01");
            }
            // 统计指定日期区间的注册人数
            int pageNo = req.get("pageNo") == null ? 1 : req.getInteger("pageNo");
            int pageSize = req.get("pageSize") == null ? 10 : req.getInteger("pageSize");
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List<PlatformMonitorInfo> pageResult = platformMonitorDao.selectRegisterUser(reqMap);
            List<PlatformMonitorInfo> result = platformMonitorDao.selectRegisterUser(reqMap);
            Map<String, List<Object>> data = new HashMap<>();

            String start = req.getString("start");
            String end = req.getString("end");
            List<String> between = BetweenDateUtils.getDaysStrings(start, end, req.getString("searchType"));
            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            List<PlatformMonitorInfo> resList = new ArrayList<>();
            for(String str:between){
                boolean res = false;
                for (PlatformMonitorInfo info : result) {
                    if(info.getCreateDate().equals(str)){
                        xList.add(info.getCreateDate());
                        yList.add(info.getUserNum());

                        resList.add(info);
                        res = true;
                        break;
                    }
                }
                if(!res){
                    xList.add(str);
                    yList.add(0);

                    PlatformMonitorInfo p = new PlatformMonitorInfo();
                    p.setCreateDate(str);
                    p.setUserNum(0);
                    resList.add(p);
                }
            }

            data.put("xList", xList);
            data.put("yList", yList);
//            bizDataJson.put("objPageList", pageResult);
            resList= resList.subList((pageNo-1)*pageSize,((pageNo-1)*pageSize+pageSize)>resList.size()?resList.size():((pageNo-1)*pageSize+pageSize));
            bizDataJson.put("objPageList", resList);
            bizDataJson.put("objList", data);
            bizDataJson.put("totalUserCount",platformMonitorDao.queryUserCount(new HashMap<>()));
            bizDataJson.put("total",between.size());
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOG.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryActivityUserStatistics(JSONObject req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(req.toJSONString(), HashMap.class);
            int pageNo = req.get("pageNo") == null ? 1 : req.getInteger("pageNo");
            int pageSize = req.get("pageSize") == null ? 10 : req.getInteger("pageSize");


            // 根据活跃人数计算相应的活跃度数据
            // ...调整总活跃人数计算方式，不去统计用户表 统计登录表
            String start = req.getString("start");
            String end = req.getString("end");
            String format = "";
            String dateType = req.getString("searchType");
            if ("year".equals(dateType)) {
                format = "yyyy";
            } else if ("month".equals(dateType)) {
                format = "yyyy-MM";
            } else {
                format = "yyyy-MM-dd";
            }
            DateFormat dateFormat = new SimpleDateFormat(format);
            if (StringUtils.isEmpty(start)) {
                Date date = platformMonitorDao.queryMostEarlyDate();
                start = dateFormat.format(date);

            }
            if (StringUtils.isEmpty(end)) {
                end = dateFormat.format(new Date());
            }
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

            reqMap.put("start", dateFormat.parse(start));
            reqMap.put("end", dateFormat.parse(end));
            // 统计指定日期区间的活跃人数
            List<PlatformMonitorInfo> result = platformMonitorDao.selectActivityUser(reqMap);

            List<Date> between = BetweenDateUtils.getDays(start, end, req.getString("searchType"));
            List<PlatformMonitorInfo> registerUserByDatePage = platformMonitorDao.selectRegisterUserByDate(between,dateType);
            List<PlatformMonitorInfo> registerUserByDate = platformMonitorDao.selectRegisterUserByDate(between,dateType);
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(2);

            // 分页数据
            TreeSet<PlatformMonitorInfo> platformMonitorInfosPage = new TreeSet<>();
            for (int i=(pageNo-1)*pageSize;i<(pageNo*pageSize)&&i<registerUserByDatePage.size();i++ ) {
                boolean flag = true;
                for (PlatformMonitorInfo activity : result) {
                    if (Objects.equals(activity.getCreateDate(),registerUserByDatePage.get(i).getCreateDate())) {
                        flag = false;
                        if (0 == registerUserByDatePage.get(i).getUserNum()) {
                            LOG.info("registerUserNum is 0");
                            registerUserByDatePage.get(i).setPercentage("0");
                        } else {
                            String percentage = numberFormat.format((float) activity.getUserNum() / (float) registerUserByDatePage.get(i).getUserNum() * 100) + "%";//所占百分比
                            LOG.info(activity.getCreateDate() + "：registerUserNum percentage is " + percentage);

                            registerUserByDatePage.get(i).setPercentage(percentage);
                        }
                        registerUserByDatePage.get(i).setUserNum(activity.getUserNum());
                        platformMonitorInfosPage.add(registerUserByDatePage.get(i));
                        break;
                    }

                }
                if (flag) {
                    registerUserByDatePage.get(i).setUserNum(0);
                    registerUserByDatePage.get(i).setPercentage("0");
                    platformMonitorInfosPage.add(registerUserByDatePage.get(i));
                }
            }
            // 无分页图表数据
            TreeSet<PlatformMonitorInfo> platformMonitorInfos = new TreeSet<>();
            for (PlatformMonitorInfo register : registerUserByDate) {
                boolean flag = true;
                for (PlatformMonitorInfo activity : result) {
                    if (Objects.equals(activity.getCreateDate(), register.getCreateDate())) {
                        flag = false;
                        if (0 == register.getUserNum()) {
                            LOG.info("registerUserNum is 0");
                            register.setPercentage("0");
                        } else {
                            String percentage = numberFormat.format((float) activity.getUserNum() / (float) register.getUserNum() * 100) + "%";//所占百分比
                            LOG.info(activity.getCreateDate() + "：registerUserNum percentage is " + percentage);

                            register.setPercentage(percentage);
                        }
                        register.setUserNum(activity.getUserNum());
                        platformMonitorInfos.add(register);
                        break;
                    }

                }
                if (flag) {
                    register.setUserNum(0);
                    register.setPercentage("0");
                    platformMonitorInfos.add(register);
                }
            }
            bizDataJson.put("objPageList", platformMonitorInfosPage);
            Map<String, List<Object>> data = new HashMap<>();

            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (PlatformMonitorInfo info : platformMonitorInfos) {
                xList.add(info.getCreateDate());
                yList.add(info.getUserNum());
            }
            data.put("xList", xList);
            data.put("yList", yList);

            bizDataJson.put("objList", data);
            bizDataJson.put("total", platformMonitorInfos.size());
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryEquipmentStatistics(JSONObject req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            int pageNo = req.get("pageNo") == null ? 1 : req.getInteger("pageNo");
            int pageSize = req.get("pageSize") == null ? 10 : req.getInteger("pageSize");
            HashMap reqMap = JSON.parseObject(req.toJSONString(), HashMap.class);
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List<PlatformMonitorInfo> pageResult = platformMonitorDao.selectEquipmentStatistics(reqMap);
            List<PlatformMonitorInfo> result = platformMonitorDao.selectEquipmentStatistics(reqMap);
            Map<String, List<Object>> data = new HashMap<>();

            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (PlatformMonitorInfo info : result) {
                xList.add(info.getBrand());
                yList.add(info.getBrandNum());
            }
            data.put("xList", xList);
            data.put("yList", yList);
            bizDataJson.put("objPageList", pageResult);
            bizDataJson.put("objList", data);
            bizDataJson.put("total", page.getTotal());
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOG.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryInstallVersionStatistics(JSONObject req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();

        try {
            HashMap reqMap = JSON.parseObject(req.toJSONString(), HashMap.class);
            int pageNo = req.get("pageNo") == null ? 1 : req.getInteger("pageNo");
            int pageSize = req.get("pageSize") == null ? 10 : req.getInteger("pageSize");
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List<PlatformMonitorInfo> pageResult = platformMonitorDao.selectInstallVersionStatistics(reqMap);
            List<PlatformMonitorInfo> result = platformMonitorDao.selectInstallVersionStatistics(reqMap);
            Map<String, List<Object>> data = new HashMap<>();

            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (PlatformMonitorInfo info : result) {
                xList.add(info.getVersion());
                yList.add(info.getVersionNum());
            }
            data.put("xList", xList);
            data.put("yList", yList);
            bizDataJson.put("objPageList", pageResult);
            bizDataJson.put("objList", data);
            bizDataJson.put("total", page.getTotal());
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOG.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryMoneyStatistics(JSONObject req) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(req.toJSONString(), HashMap.class);

            List<HashMap<String, Object>> moneyList = platformMonitorDao.queryMoneyList(reqMap);

            Map<String, List<Object>> data = new HashMap<>();
            List<Object> xList = new ArrayList<>();
            List<Object> yList = new ArrayList<>();
            for (HashMap<String, Object> dataMap : moneyList) {
                xList.add(dataMap.get("dateStr"));
                yList.add(dataMap.get("totalprice"));
            }
            data.put("xList", xList);
            data.put("yList", yList);
            bizDataJson.put("objList", data);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }


    public static void main(String[] args) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date parse = sdf.parse("2021-06");
            System.out.println(sdf2.format(parse));

        }catch (Exception e){

        }
    }
}
