package com.centit.shopping.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.CarouselService;
import com.centit.shopping.dao.ShoppingCarouselDao;
import com.centit.shopping.dao.TicketProjectDao;
import com.centit.shopping.po.ShoppingCarousel;
import com.centit.shopping.po.TicketProject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/22
 */
@Service
public class CarouselServiceImpl implements CarouselService {
    public static final Log log = LogFactory.getLog(CarouselService.class);

    @Resource
    private ShoppingCarouselDao carouselDao;

    @Resource
    private TicketProjectDao ticketProjectDao;

    @Override
    public JSONObject queryList(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";

        if (StringUtils.isEmpty(param.getString("type"))) {
            retJson.put("retCode", retCode);
            retJson.put("retMsg", retMsg);
            return retJson;
        }
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSONObject.parseObject(param.toJSONString(), HashMap.class);
            reqMap.put("status", 1);
            List<ShoppingCarousel> shoppingCarousels = carouselDao.queryList(reqMap);

            if(param.getString("type").equals("1")){  //演出
                List<ShoppingCarousel> resList = new ArrayList<>();
                for(ShoppingCarousel shoppingCarousel:shoppingCarousels){
                    if(shoppingCarousel.getCategory().equals("2")){
                        String projectId = shoppingCarousel.getValue();
                        TicketProject ticketProject = new TicketProject();
                        ticketProject.setProjectId(projectId);
                        ticketProject = ticketProjectDao.queryDetail(ticketProject);
                        if(ticketProject!=null&&ticketProject.getProjectSaleState()==2){
                            resList.add(shoppingCarousel);
                        }
                    }else{
                        resList.add(shoppingCarousel);
                    }
                }
                bizDataJson.put("objList", resList);
            }else{
                bizDataJson.put("objList", shoppingCarousels);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("bizData", bizDataJson);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }
}
