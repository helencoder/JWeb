package com.helencoder.config;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * Application config
 *
 * Created by zhenghailun on 2018/3/18.
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    private Environment env;

    @Value("${spark.app.name}")
    private String appName;

    @Value("${spark.home}")
    private String sparkHome;

    @Value("${spark.master.uri}")
    private String masterUri;

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName(appName)
                .setSparkHome(sparkHome)
                .setMaster(masterUri)
                .set("spark.testing.memory", "2147480000");
    }

//    @Bean
//    public JavaSparkContext javaSparkContext() {
//        return new JavaSparkContext(sparkConf());
//    }

    @Bean
    public SparkContext sparkContext() {
        return new SparkContext(sparkConf());
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(sparkContext())
                .appName(appName)
                .getOrCreate();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
