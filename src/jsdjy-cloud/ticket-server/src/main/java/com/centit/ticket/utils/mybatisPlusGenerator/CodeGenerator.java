package com.centit.ticket.utils.mybatisPlusGenerator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: CodeGeneratorPackageConfig
 * @Description: 代码生成工具类。运行这里的main方法即可自动生成代码
 * @Author cui_jian
 * @Date 2020-04-09 16:32
 */
public class CodeGenerator {


    /**
     * MySQL/Oracle 生成
     */
    public static void main(String[] args) {

        // 自定义需要填充的字段
        // List<TableFill> tableFillList = new ArrayList<>();
        // tableFillList.add(new TableFill("ASDD_SS", FieldFill.INSERT_UPDATE));

        AutoGenerator mpg = new AutoGenerator();
        // 选择 freemarker 引擎，默认 Veloctiy
        // mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(GeneratorConstants.PROJECT_DIR + GeneratorConstants.JAVA_DIR);
//        gc.setFileOverride(true);// 是否覆盖文件
        gc.setFileOverride(false);// 是否覆盖文件
        gc.setActiveRecord(true);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(true);// XML columList
        //gc.setKotlin(true);//是否生成 kotlin 代码
        gc.setAuthor(GeneratorConstants.AUTOR);
        //时间类型
        gc.setDateType(DateType.ONLY_DATE);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sDao");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        //       dsc.setDbType(DbType.ORACLE);
        /*dsc.setTypeConvert(new MySqlTypeConvert(){
            // 自定义数据库表字段类型转换【可选】
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }
        });*/
        dsc.setDriverName(GeneratorConstants.DB_DRIVER);
        dsc.setUrl(GeneratorConstants.DB_URL);
        dsc.setUsername(GeneratorConstants.DB_USER_NAME);
        dsc.setPassword(GeneratorConstants.DB_PASSWORD);
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 全局大写命名 ORACLE 注意
        // strategy.setCapitalMode(true);
        // 此处可以修改为您的表前缀
        //strategy.setTablePrefix(new String[] { "tlog_", "tsys_" });
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表,注释掉生成全部表
        // String tables = "";
        // StringUtils.split(tables);
        strategy.setInclude(StringUtils.split(GeneratorConstants.TABLES, ","));
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        // strategy.setSuperEntityClass("com.baomidou.demo.TestEntity");
        // 自定义实体，公共字段
        // strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
        // 自定义 mapper 父类
        // strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");
        // 自定义 service 父类
        // strategy.setSuperServiceClass("com.baomidou.demo.TestService");
        // 自定义 service 实现类父类
        // strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
        // 自定义 controller 父类
        // strategy.setSuperControllerClass("com.baomidou.demo.TestController");
        // 【实体】是否生成字段常量（默认 false）
        // public static final String ID = "test_id";
        //strategy.setEntityColumnConstant(true);
        // 【实体】是否为构建者模型（默认 false）
        // public User setName(String name) {this.name = name; return this;}
        //strategy.setEntityBuilderModel(true);
        // 【实体】是否为lombok模型（默认 false）<a href="https://projectlombok.org/">document</a>
        strategy.setEntityLombokModel(true);
        // strategy.setTableFillList(tableFillList);
        mpg.setStrategy(strategy);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(GeneratorConstants.MODULE);
        pc.setParent(GeneratorConstants.PARENT_PACKAGE);
        pc.setController(GeneratorConstants.CHILD_PACKAGE_CONTROLLER);
        pc.setEntity(GeneratorConstants.CHILD_PACKAGE_ENTITY);
        pc.setMapper(GeneratorConstants.CHILD_PACKAGE_DAO);
        pc.setXml(GeneratorConstants.CHILD_PACKAGE_MAPPERXML);
        pc.setService(GeneratorConstants.CHILD_PACKAGE_SERVICE);
        pc.setServiceImpl(GeneratorConstants.CHILD_PACKAGE_SERVICEIMPL);
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】  ${cfg.abc}
        // InjectionConfig cfg = new InjectionConfig() {
        //     @Override
        //     public void initMap() {
        //         Map<String, Object> map = new HashMap<String, Object>();
        //         map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
        //         this.setMap(map);
        //     }
        // };

        // 自定义 xxListIndex.html 生成
        // List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
        // focList.add(new FileOutConfig("/templates/list.html.vm") {
        //     @Override
        //     public String outputFile(TableInfo tableInfo) {
        //         // 自定义输入文件名称
        //         return "F:/idea-maven/maven/src/main/resources/static/" + tableInfo.getEntityName() + "ListIndex.html";
        //     }
        // });
        // cfg.setFileOutConfigList(focList);
        // mpg.setCfg(cfg);


        // 关闭默认 xml 生成，调整生成 至 根目录
        /*TemplateConfig tc = new TemplateConfig();
        tc.setXml(null);
        mpg.setTemplate(tc);*/

        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/templates 下面内容修改，
        // 放置自己项目的 src/main/resources/templates 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        TemplateConfig tc = new TemplateConfig();
        // tc.setController("/templates/Controller.java.vm");
        // tc.setService("/templates/Service.java.vm");
        // tc.setServiceImpl("/templates/ServiceImpl.java.vm");
        // tc.setEntity("/templates/Entity.java.vm");
        // tc.setMapper("/templates/Mapper.java.vm");
        // tc.setXml("/templates/Mapper.xml.vm");
        // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
        mpg.setTemplate(tc);

        // 执行生成
        mpg.execute();

        // 打印注入设置【可无】
        // System.err.println(mpg.getCfg().getMap().get("abc"));
    }


}
