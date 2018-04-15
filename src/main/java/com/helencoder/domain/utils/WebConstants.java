package com.helencoder.domain.utils;

import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

/**
 * Web常量配置
 *
 * Created by zhenghailun on 2018/3/26.
 */
public class WebConstants {
    // 项目根目录绝对路径
    public static final String ROOT_PATH = System.getProperty("user.dir");

    // classpath
    public static String getClassPath() {
        String classPath = "";
        try {
            classPath = ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return classPath;
    }

    // 接口状态码
    public static final int CODE_OK = 200;
    public static final int CODE_ERROR = -1;

    public static final String REQUEST_EXCEPTION = "request_exception";
    public static final String RESPONSE_EXCEPTION = "response_exception";



}
