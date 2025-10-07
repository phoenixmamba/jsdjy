package com.centit.scan.utils;

import com.centit.scan.webmgr.po.ShoppingStore;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {

    private static <D> D getCtxBean(String beanName, Class<D> clazz) {
        WebApplicationContext ctx = ContextLoaderListener
                .getCurrentWebApplicationContext();
        return ctx.getBean(beanName, clazz);
    }

    /**
     * 获取官方默认店铺
     */
    public static ShoppingStore getSystemStore() {
        ShoppingStore shoppingStore = new ShoppingStore();
        shoppingStore.setId("1");
        return CommonInit.staticShoppingStoreDao.queryDetail(shoppingStore);
    }

}