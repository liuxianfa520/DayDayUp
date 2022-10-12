package com;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * mybatis plus代码生成
 */
public class CodeGenerator {
    /**
     * 作者
     */
    static String author = "安小乐";
    /**
     * 项目名
     */
    static String projectName = "junit\\06-mybatis-mapper-print-sql";
    /**
     * 需要生成的表,
     */
    static String[] tableName = {"user", "customer"};
    /**
     * 表前缀
     */
    static String tablePrefix = "";


    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        String outputDir = projectPath + "/" + projectName + "/src/main/java";
        gc.setOutputDir(outputDir.replace("\\", "/"));
        gc.setFileOverride(true);
        gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(true);// XML columList
        gc.setAuthor(author);
        gc.setOpen(false);
//        gc.setSwagger2(true); //实体属性 Swagger2 注解
        gc.setServiceName("%sService");

        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setUrl("jdbc:mysql://localhost:13306/test?serverTimezone=Asia/Shanghai&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.xxx");
        pc.setModuleName(null);
        pc.setMapper("mapper");
        pc.setEntity("domain");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("com.xxx.domain.BaseDomain");
//        strategy.setRestControllerStyle(true);
        strategy.setEntityLombokModel(true);
        strategy.setInclude(tableName);
        strategy.setTablePrefix(tablePrefix);
        mpg.setStrategy(strategy);
        mpg.execute();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("代码生成到:" + gc.getOutputDir());
    }
}
