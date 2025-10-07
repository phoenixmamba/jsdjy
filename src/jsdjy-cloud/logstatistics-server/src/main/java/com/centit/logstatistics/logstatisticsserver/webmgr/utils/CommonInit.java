package com.centit.logstatistics.logstatisticsserver.webmgr.utils;

import com.centit.logstatistics.logstatisticsserver.webmgr.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
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
	private ShoppingUserDao shoppingUserDao;
	@Autowired
	private FUserinfoDao fUserinfoDao;

	@Autowired
	private ShoppingArtactivitySignupinfoDao shoppingArtactivitySignupinfoDao;
	@Autowired
	private ShoppingArtplanSignupinfoDao shoppingArtplanSignupinfoDao;
	@Autowired
	private ShoppingSysconfigDao shoppingSysconfigDao;


	public static FUserinfoDao staticFUserinfoDao;
	public static ShoppingUserDao staticShoppingUserDao;

	public static ShoppingArtactivitySignupinfoDao staticShoppingArtactivitySignupinfoDao;
	public static ShoppingArtplanSignupinfoDao staticShoppingArtplanSignupinfoDao;
	public static ShoppingSysconfigDao staticShoppingSysconfigDao;

	@PostConstruct
	public void init(){
		System.out.println("============222");
		staticFUserinfoDao = fUserinfoDao;
		staticShoppingUserDao = shoppingUserDao;

		staticShoppingArtactivitySignupinfoDao = shoppingArtactivitySignupinfoDao;
		staticShoppingArtplanSignupinfoDao = shoppingArtplanSignupinfoDao;
		staticShoppingSysconfigDao = shoppingSysconfigDao;

	}



}
