package com.helencoder;

import com.helencoder.dao.DeepModelDao;
import com.helencoder.dao.SparkModelDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring启动任务处理
 * 1、直接加载模型进内存
 *
 * Created by zhenghailun on 2018/3/31.
 */
//@Component
public class InitializingService implements InitializingBean {

    @Autowired
    private SparkModelDao sparkModelDao;

    @Autowired
    private DeepModelDao deepModelDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.err.println("直接加载模型进内存");
        // 模拟数据请求？
        System.err.println("加载机器模型");
        sparkModelDao.getLogisticRegressionModel();
        System.err.println("加载词向量模型");
        deepModelDao.getWordVectors();
        System.err.println("加载深度模型");
        deepModelDao.getNet();
    }
}
