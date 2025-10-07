//package com.centit.zuulgateway.utils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
//
///**
// *
// * @Version : 1.0
// * @Author : cui_jian
// * @Description : 通用装载类
// * @Date : 2020年08月11日 下午4:33:20
// */
//@Component
//public class CommonInit {
//
//	@Value("${uploadFiledir_common}")
//	private String uploadFiledir_common;  //上传文件目录
//	@Value("${uploadFilePrefix_common}")
//	private String uploadFilePrefix_common;  //上传文件目录显示路径前缀
//
//
//	public static String uploadFiledir_common_static;
//	public static String uploadFilePrefix_common_static;
//
//
//
//	@PostConstruct
//	public void init(){
//
//		uploadFiledir_common_static = uploadFiledir_common;
//		uploadFilePrefix_common_static = uploadFilePrefix_common;
//
//
//	}
//
//
//
//}
