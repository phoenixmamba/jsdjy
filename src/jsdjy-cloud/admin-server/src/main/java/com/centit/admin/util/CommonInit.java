package com.centit.admin.util;

import com.centit.admin.system.dao.FDatadictionaryDao;
import com.centit.admin.system.dao.FUnitinfoDao;
import com.centit.admin.system.dao.FUserunitDao;
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
	private FDatadictionaryDao fDatadictionaryDao;
	@Autowired
	private FUserunitDao fUserunitDao;
	@Autowired
	private FUnitinfoDao fUnitinfoDao;

	public static FDatadictionaryDao staticFDatadictionaryDao;
	public static FUserunitDao staticFUserunitDao;
	public static FUnitinfoDao staticFUnitinfoDao;

	@PostConstruct
	public void init(){
		System.out.println("============222");
		staticFDatadictionaryDao= fDatadictionaryDao;
		staticFUserunitDao = fUserunitDao;
		staticFUnitinfoDao = fUnitinfoDao;
	}



}
