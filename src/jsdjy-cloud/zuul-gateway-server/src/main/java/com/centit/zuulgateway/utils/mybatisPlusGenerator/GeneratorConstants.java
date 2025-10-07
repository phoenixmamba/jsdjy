package com.centit.zuulgateway.utils.mybatisPlusGenerator;

/**
 * @ClassName: GeneratorConfig
 * @Description: 代码生成配置类
 * @Author cui_jian
 * @Date 2020-04-09 16:04
 */
public class GeneratorConstants {

    /**
     * 项目路径
     **/
//    public static final String PROJECT_DIR = "D:/GitRepository/JSSJTT/macpjsjtt/master/SRC/spring-cloud/zuul-gateway-server";
//     public static final String PROJECT_DIR = "D:/develop/workspace/macpjsjtt/SRC/spring-cloud/zuul-gateway-server";
    public static final String PROJECT_DIR = "D:/develop/workspace/jsdjy/SRC/jsdjy-cloud/zuul-gateway-server";

    /**
     * java类目录
     **/
    public static final String JAVA_DIR = "/src/main/java";
    /**
     * 作者
     **/
    public static final String AUTOR = "cui_jian";
    /**
     * 数据库配置
     **/
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://192.168.132.28:3306/shop_jsdjytest";
    public static final String DB_USER_NAME = "root";
    public static final String DB_PASSWORD = "Centit@001";
    /**
     * 指定生成的表 ， 用 ","分割
     **/
    public static final String TABLES = "t_concurrency_switch";
    /**
     * 模块名称
     **/
    public static final String MODULE = "zuulgateway";
    /**
     * 父级包名
     **/
    public static final String PARENT_PACKAGE = "com.centit";
//    public static final String PARENT_PACKAGE = "com.centit.zuulgateway.utils.mybatisPlusGenerator";
    /**
     * 子级controller前端控制器所在包名。释义:生成的controller前端控制器文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_CONTROLLER = "controller";
    /**
     * 子级entity实体类所在包名。释义:生成的entity实体类文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_ENTITY = "po";
    /**
     * 子级dao/Mapper接口类所在包名。释义:生成的dao/Mapper接口类文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_DAO = "dao";
    /**
     * 子级Mapper.xml文件所在包名。释义:生成的Mapper.xml文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_MAPPERXML = "dao";
    /**
     * 子级service服务接口文件所在包名。释义:生成的service服务接口文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_SERVICE = "service";
    /**
     * 子级service.impl服务接口实现类文件所在包名。释义:生成的service.impl服务接口实现类文件所在文件夹的名字
     **/
    public static final String CHILD_PACKAGE_SERVICEIMPL = "service.impl";


}
