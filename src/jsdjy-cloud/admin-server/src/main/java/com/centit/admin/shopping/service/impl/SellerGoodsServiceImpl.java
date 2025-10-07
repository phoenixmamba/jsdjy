package com.centit.admin.shopping.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.redis.RedissonRedisDataService;
import com.centit.core.consts.RedisConst;
import com.centit.core.consts.StoreConst;
import com.centit.admin.shopping.dao.*;
import com.centit.admin.shopping.dto.*;
import com.centit.admin.shopping.po.*;
import com.centit.admin.shopping.service.GoodsStockService;
import com.centit.admin.shopping.service.SellerGoodsService;
import com.centit.admin.threadPool.ThreadPoolExecutorFactory;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/

@Service
@Slf4j
public class SellerGoodsServiceImpl implements SellerGoodsService {

    @Resource
    private GoodsInfoDao goodsInfoDao;
    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private GoodsSpecDao goodsSpecDao;
    @Resource
    private ShoppingSpecificationDao shoppingSpecificationDao;
    @Resource
    private ShoppingSpecpropertyDao shoppingSpecpropertyDao;
    @Resource
    private GoodsPhotoDao goodsPhotoDao;
    @Resource
    private GoodsSpecInventoryDao goodsSpecInventoryDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private GoodsStockService goodsStockService;

    @Resource
    private RedissonRedisDataService redissonRedisDataService;

    /**
     * 查询商户商品列表
     */
    @Override
    public Result queryPageList(JSONObject reqJson) {
        JSONObject bizDataJson = new JSONObject();
        int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
        int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");

        HashMap reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
        reqMap.put("deleteStatus","0");
        //所属商户，默认官方商户
        reqMap.put("goodsStoreId", StoreConst.DEFAULT_STORE_ID);
        Page<GoodsInfoPo> goodsInfoPoPage = PageHelper.startPage(pageNo, pageSize);
        List<GoodsInfoPo> goodsList = goodsInfoDao.queryStoreList(reqMap);
        bizDataJson.put("total", goodsInfoPoPage.getTotal());
        for(GoodsInfoPo goodsInfoPo :goodsList){
            reqMap.clear();
            reqMap.put("goodsId", goodsInfoPo.getId());
            goodsInfoPo.setSoldCount(shoppingGoodscartDao.queryGoodsSoldCount(reqMap));
        }
        bizDataJson.put("objList",goodsList);
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * 校验商品名称是否已存在
     */
    @Override
    public Result checkGoodsName(JSONObject reqJson) {
        JSONObject bizDataJson = new JSONObject();
        HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
        reqMap.put("deleteStatus","0");
        bizDataJson.put("res", goodsInfoDao.checkGoodsName(reqMap));
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * @Description 上/下架商品
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result putGoods(JSONObject reqJson) {
        String id=reqJson.getString("id");
        Arrays.asList(id.split(",")).forEach(idStr->{
            GoodsInfoPo goodsInfoPo = new GoodsInfoPo();
            goodsInfoPo.setId(idStr);
            goodsInfoPo.setGoodsStatus(reqJson.getString("goodsStatus"));
            goodsInfoDao.updateStatus(goodsInfoPo);
            if("1".equals(goodsInfoPo.getGoodsStatus())){
                redissonRedisDataService.deleteKey(RedisConst.KEY_INFO_GOODS + goodsInfoPo.getId());
            }
        });
        return Result.defaultSuccess();
    }

    /**
     * @Description 删除商品
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result delGoods(JSONObject reqJson) {
        GoodsInfoPo goodsInfoPo = JSON.parseObject(reqJson.toJSONString(), GoodsInfoPo.class);
        goodsInfoPo.setDeleteStatus("1");
        goodsInfoDao.update(goodsInfoPo);
        redissonRedisDataService.deleteKey(RedisConst.KEY_INFO_GOODS + goodsInfoPo.getId());
        return Result.defaultSuccess();
    }

    /**
     * 查询商品分类列表
     */
    @Override
    public Result queryGoodsClass(JSONObject reqJson) {
        JSONObject bizDataJson = new JSONObject();
        HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
        reqMap.put("deleteStatus","0");
        bizDataJson.put("objList",shoppingGoodsclassDao.queryPageList(reqMap));
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * 获取商品详情
     */
    @Override
    public Result goodsDetail(String goodsId) {
        JSONObject bizDataJson = new JSONObject();
        GoodsInfoPo goodsInfoPo = new GoodsInfoPo();
        goodsInfoPo.setId(goodsId);
        //查询商品主体信息
        goodsInfoPo = goodsInfoDao.queryDetail(goodsInfoPo);
        //商品库存
        goodsInfoPo.setGoodsInventory(goodsStockService.getGoodsStock(goodsId));
        //商品规格库存
        HashMap<String, Object> reqMap =new HashMap<>(2);
        reqMap.put("goodsId",goodsId);
        JSONArray inventoryDetailArray = new JSONArray();
        HashSet<String> propertyIds = new HashSet<>();
        List<GoodsSpecInventoryPo> inventoryList= goodsSpecInventoryDao.queryList(reqMap);
        if(!inventoryList.isEmpty()){
            inventoryList.forEach(goodsSpecInventoryPo ->{
                JSONObject obj = new JSONObject();
                obj.put("id", goodsSpecInventoryPo.getPropertys());
                obj.put("count",goodsStockService.getGoodsStockWithPropertys(goodsId, goodsSpecInventoryPo.getPropertys()));
                obj.put("price", goodsSpecInventoryPo.getPrice().toString());
                String[] sps= goodsSpecInventoryPo.getPropertys().split("_");
                String valueStr="";
                for(int m=0;m<sps.length;m++){
                    String propertyId = sps[m];
                    propertyIds.add(propertyId);
                    ShoppingSpecproperty goodsspecproperty = new ShoppingSpecproperty();
                    goodsspecproperty.setId(propertyId);
                    goodsspecproperty = shoppingSpecpropertyDao.queryDetail(goodsspecproperty);
                    valueStr+=goodsspecproperty.getValue()+"_";
                    obj.put("value",valueStr);
                }
                inventoryDetailArray.add(obj);
            });
        }
        //异步查询规格信息
        ExecutorService executorService = ThreadPoolExecutorFactory.createThreadPoolExecutor();
        String gcId = goodsInfoPo.getGcId();
        CompletableFuture<List<ShoppingSpecification>> future= CompletableFuture.supplyAsync(()-> querySpecs(gcId,propertyIds),executorService);

        //商品信息
        bizDataJson.put("detail", goodsInfoPo);
        //规格库存
        bizDataJson.put("inventoryDetails",inventoryDetailArray);
        //商品图片
        List<GoodsPhotoPo> photos = goodsPhotoDao.selectGoodsPhotos(goodsId);
        bizDataJson.put("photos",photos);
        //规格信息
        try {
            List<ShoppingSpecification> specs = future.get();
            if(specs!=null){
                bizDataJson.put("specs",specs);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步查询商品规格属性异常：",e);
            throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION.getCode(),"获取商品规格属性信息失败");
        }
        return Result.defaultSuccess(bizDataJson);
    }

    private List<ShoppingSpecification> querySpecs(String gcId, HashSet<String> propertyIds){

        ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
        shoppingGoodsclass.setId(gcId);
        shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
        while((shoppingGoodsclass.getGoodstypeId()==null||"".equals(shoppingGoodsclass.getGoodstypeId()))&&shoppingGoodsclass.getParentId()!=null){
            shoppingGoodsclass.setId(shoppingGoodsclass.getParentId());
            shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
        }
        if(shoppingGoodsclass.getGoodstypeId()!=null&&!"".equals(shoppingGoodsclass.getGoodstypeId())){
            List<ShoppingSpecification> specs = shoppingSpecificationDao.queryTypeSpecs(shoppingGoodsclass.getGoodstypeId());
            HashMap<String, Object> specMap = new HashMap<>(2);
            specMap.put("deleteStatus","0");
            for(ShoppingSpecification shoppingSpecification :specs){
                specMap.put("specId", shoppingSpecification.getId());
                List<ShoppingSpecproperty> propertys = shoppingSpecpropertyDao.queryList(specMap);
                propertys.forEach(shoppingSpecproperty ->{
                    if(propertyIds.contains(shoppingSpecproperty.getId())){
                        shoppingSpecproperty.setHasChosen(true);
                    }
                });
                shoppingSpecification.setPropertys(propertys);
            }
            return specs;
        }
        return null;
    }

    /**
     * 查询商品初始规格配置参数
     */
    @Override
    public Result queryDefaultSpecification(JSONObject reqJson) {
        JSONObject bizDataJson = new JSONObject();
        String gcId=reqJson.getString("gcId");
        ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
        shoppingGoodsclass.setId(gcId);
        shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
        while(StrUtil.isBlank(shoppingGoodsclass.getGoodstypeId())&&shoppingGoodsclass.getParentId()!=null){
            shoppingGoodsclass.setId(shoppingGoodsclass.getParentId());
            shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
        }
        if(StrUtil.isBlank(shoppingGoodsclass.getGoodstypeId())){
            List<ShoppingSpecification> specs = shoppingSpecificationDao.queryTypeSpecs(shoppingGoodsclass.getGoodstypeId());
            HashMap<String, Object> reqMap = new HashMap<>(2);
            reqMap.put("deleteStatus","0");
            for(ShoppingSpecification shoppingSpecification :specs){
                reqMap.put("specId", shoppingSpecification.getId());
                shoppingSpecification.setPropertys(shoppingSpecpropertyDao.queryList(reqMap));
            }
            bizDataJson.put("specs",specs);
        }
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * @Description 添加新商品
     **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result addGoods(GoodInfoDTO shoppingGoodsDto) {
        GoodsInfoPo goodsInfoPo =new GoodsInfoPo(shoppingGoodsDto);
        goodsInfoPo.setGoodsStoreId(StoreConst.DEFAULT_STORE_ID);
        goodsInfoDao.insert(goodsInfoPo);
        String goodsId = goodsInfoPo.getId();
        //保存规格信息
        saveSpecs(shoppingGoodsDto.getSpecs(),goodsId);
        //保存图片信息
        savePhotos(shoppingGoodsDto.getPhotos(),goodsId);
        //规格库存
        if(shoppingGoodsDto.getInventoryDetails()!=null){
            List<GoodsSpecInventoryDTO> inventoryDetails =shoppingGoodsDto.getInventoryDetails();
            List<GoodsSpecInventoryPo> goodsSpecInventoryPoList = inventoryDetails.stream()
                    .map(inventoryDetail -> {
                        GoodsSpecInventoryPo goodsSpecInventoryPo = new GoodsSpecInventoryPo(inventoryDetail);
                        goodsSpecInventoryPo.setGoodsId(goodsId);
                        return goodsSpecInventoryPo;
                    }).collect(Collectors.toList());
            goodsSpecInventoryDao.batchInsertGoodsInventorys(goodsSpecInventoryPoList);
            //保存库存数据到redis
            goodsStockService.initGoodsStockWithPropertyList(goodsId, goodsInfoPo.getGoodsInventory(), goodsSpecInventoryPoList);
        }else{
            goodsStockService.initGoodsStock(goodsId, goodsInfoPo.getGoodsInventory());
        }
        JSONObject bizDataJson = new JSONObject();
        bizDataJson.put("id",goodsId);
        return Result.defaultSuccess(bizDataJson);
    }

    /**
     * 编辑商品信息
     * 注：所有商品只有在下架状态才可以编辑
     * @param shoppingGoodsDto
     * @return Result
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result editGoods(GoodInfoDTO shoppingGoodsDto) {
        //商品信息
        GoodsInfoPo goodsInfoPo =new GoodsInfoPo(shoppingGoodsDto);
        goodsInfoPo.setGoodsStoreId(StoreConst.DEFAULT_STORE_ID);
        goodsInfoDao.update(goodsInfoPo);
        String goodsId= goodsInfoPo.getId();
        //保存图片信息
        savePhotos(shoppingGoodsDto.getPhotos(),goodsId);
//        //保存规格信息
//        saveSpecs(shoppingGoodsDto.getSpecs(),goodsId);
//        if(shoppingGoodsDto.getInventoryDetails()!=null){
//            //先删除已有配置
//            ShoppingGoodsInventoryPO goodsInventory = new ShoppingGoodsInventoryPO();
//            goodsInventory.setGoodsId(goodsId);
//            shoppingGoodsInventoryDao.delete(goodsInventory);
//            List<GoodsInventoryDTO> inventoryDetails =shoppingGoodsDto.getInventoryDetails();
//            List<ShoppingGoodsInventoryPO> shoppingGoodsInventoryPoList = inventoryDetails.stream()
//                    .map(inventoryDetail -> {
//                        ShoppingGoodsInventoryPO shoppingGoodsInventoryPO = new ShoppingGoodsInventoryPO(inventoryDetail);
//                        shoppingGoodsInventoryPO.setGoodsId(goodsId);
//                        return shoppingGoodsInventoryPO;
//                    }).collect(Collectors.toList());
//            shoppingGoodsInventoryDao.batchInsertGoodsInventorys(shoppingGoodsInventoryPoList);
//            //保存库存数据到redis
//            goodsStockService.initGoodsStockWithPropertys(goodsId,shoppingGoodsPo.getGoodsInventory(),shoppingGoodsInventoryPoList);
//        }else{
//            goodsStockService.initGoodsStock(goodsId,shoppingGoodsPo.getGoodsInventory());
//        }
        String goodsKey = RedisConst.KEY_INFO_GOODS + goodsInfoPo.getId();
        redissonRedisDataService.deleteKey(goodsKey);
        return Result.defaultSuccess();
    }

    @Override
    public Result cutGoodsStock(GoodsStockDTO stockDTO) {
        goodsStockService.cutGoodsStock(stockDTO.getGoodsId(),stockDTO.getPropertys(),stockDTO.getStock());
        return Result.defaultSuccess();
    }

    @Override
    public Result addGoodsStock(GoodsStockDTO stockDTO) {
        goodsStockService.addGoodsStock(stockDTO.getGoodsId(),stockDTO.getPropertys(),stockDTO.getStock());
        return Result.defaultSuccess();
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void saveSpecs(List<GoodsSpecDTO> specs, String goodsId){
        Optional.ofNullable(specs).ifPresent(specDTOList->{
            List<GoodsSpecPo> goodsSpecPoList =specDTOList.stream().map(specDto -> {
                GoodsSpecPo specPo = new GoodsSpecPo(specDto);
                specPo.setGoodsId(goodsId);
                return specPo;
            }).collect(Collectors.toList());
            //保存规格信息到数据库
            goodsSpecDao.batchInsertGoodsSpecs(goodsSpecPoList);
        });
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public void savePhotos(List<GoodsPhotoDTO> photos, String goodsId){
        GoodsPhotoPo photo = new GoodsPhotoPo();
        photo.setGoodsId(goodsId);
        goodsPhotoDao.delete(photo);
        Optional.ofNullable(photos).ifPresent(photoDTOList->{
            List<GoodsPhotoPo> shoppingGoodsSpecPoList=photoDTOList.stream().map(photoDto -> {
                GoodsPhotoPo photoPo = new GoodsPhotoPo(photoDto);
                photoPo.setGoodsId(goodsId);
                return photoPo;
            }).collect(Collectors.toList());
            //保存图片信息到数据库
            goodsPhotoDao.batchInsertGoodsPhotos(shoppingGoodsSpecPoList);
        });
    }


}
