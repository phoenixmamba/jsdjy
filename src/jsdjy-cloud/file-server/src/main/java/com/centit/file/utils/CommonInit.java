package com.centit.file.utils;

import com.centit.file.webmgr.dao.ShoppingStoreDao;
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

    public static ShoppingStoreDao staticShoppingStoreDao;

	@PostConstruct
	public void init(){
		System.out.println("============222");
        staticShoppingStoreDao=shoppingStoreDao;
	}



}
