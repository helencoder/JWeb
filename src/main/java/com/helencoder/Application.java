package com.helencoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring 启动脚本
 *
 * Created by helencoder on 2018/2/6.
 */
//@ComponentScan(basePackages = "com.helencoder")
//@EnableAutoConfiguration
//public class com.helencoder.Application {
//    public static void main(String[] args) {
//        SpringApplication.run(com.helencoder.Application.class, args);
//    }
//}


@EnableCaching
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}