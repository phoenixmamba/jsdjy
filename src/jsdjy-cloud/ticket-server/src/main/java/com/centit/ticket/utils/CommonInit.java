package com.centit.ticket.utils;

import com.centit.ticket.dao.*;
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
	private ShoppingStoreDao shoppingStoreDao;
	@Autowired
	private ShoppingUserDao shoppingUserDao;
	@Autowired
	private ShoppingGoodscartDao shoppingGoodscartDao;
	@Autowired
	private ShoppingStorecartDao shoppingStorecartDao;
	@Autowired
	private TicketClassDao ticketClassDao;
	@Autowired
	private CrmAuthorizationDao crmAuthorizationDao;
	@Autowired
	private TLmThirdlogDao tLmThirdlogDao;
	@Autowired
	private ShoppingAssetRuleDao shoppingAssetRuleDao;
	@Autowired
	JedisPoolUtils jedisPoolUtils;

	public static ShoppingAssetRuleDao staticShoppingAssetRuleDao;
	public static TLmThirdlogDao staticTLmThirdlogDao;
	public static CrmAuthorizationDao staticCrmAuthorizationDao;
	public static ShoppingStoreDao staticShoppingStoreDao;
	public static ShoppingUserDao staticShoppingUserDao;
    public static ShoppingGoodscartDao staticShoppingGoodscartDao;
	public static ShoppingStorecartDao staticshoppingStorecartDao;
	public static TicketClassDao staticTicketClassDao;
	public static JedisPoolUtils staticJedisPoolUtils;

	@PostConstruct
	public void init(){
		System.out.println("============222");
		staticJedisPoolUtils=jedisPoolUtils;
		staticShoppingAssetRuleDao=shoppingAssetRuleDao;
		staticShoppingStoreDao=shoppingStoreDao;
		staticShoppingUserDao = shoppingUserDao;
		staticShoppingGoodscartDao = shoppingGoodscartDao;
		staticshoppingStorecartDao = shoppingStorecartDao;
		staticTicketClassDao = ticketClassDao;
		staticCrmAuthorizationDao=crmAuthorizationDao;
		staticTLmThirdlogDao = tLmThirdlogDao;

	}



}
