package com.centit.pay.utils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.centit.pay.biz.dao.*;
import com.centit.pay.sdk.unionpay.SDKConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 
 * @Version : 1.0
 * @Author : cui_jian
 * @Description : 通用装载类
 * @Date : 2017年11月14日 下午4:33:20
 */
@Component
public class CommonInit {



	@Autowired
	private ShoppingOrderPaylogDao shoppingOrderPaylogDao;
	@Autowired
	private ShoppingUserDao shoppingUserDao;
	@Autowired
	private CrmAuthorizationDao crmAuthorizationDao;
	@Autowired
	private TLmThirdlogDao tLmThirdlogDao;
	@Autowired
	private ShoppingOrderLogDao shoppingOrderLogDao;
	@Autowired
	private ShoppingAssetRecordDao shoppingAssetRecordDao;

	public static ShoppingAssetRecordDao staticShoppingAssetRecordDao;
	public static TLmThirdlogDao staticTLmThirdlogDao;
	public static ShoppingOrderPaylogDao staticShoppingOrderPaylogDao;
	public static ShoppingUserDao staticShoppingUserDao;
	public static CrmAuthorizationDao staticCrmAuthorizationDao;
	public static ShoppingOrderLogDao staticShoppingOrderLogDao;
	@PostConstruct
	public void init(){
		staticShoppingAssetRecordDao=shoppingAssetRecordDao;
		staticShoppingOrderPaylogDao=shoppingOrderPaylogDao;
		staticShoppingUserDao = shoppingUserDao;
		staticCrmAuthorizationDao = crmAuthorizationDao;
		staticTLmThirdlogDao = tLmThirdlogDao;
		staticShoppingOrderLogDao = shoppingOrderLogDao;
	}



}
