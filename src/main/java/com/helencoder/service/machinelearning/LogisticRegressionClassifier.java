package com.helencoder.service.machinelearning;

import com.helencoder.dao.SparkModelDao;
import com.helencoder.domain.utils.BasicUtil;
import com.helencoder.domain.utils.WebConstants;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Logistic Regression
 *
 * Created by helencoder on 2018/3/5.
 */
@Component
public class LogisticRegressionClassifier implements Serializable {

    @Autowired
    private transient SparkSession sparkSession;

    @Autowired
    private transient SparkModelDao sparkModelDao;

    public String run(String content) throws IOException {

        // 首先进行判断对应模型目录是否存在
        String modelPath = WebConstants.getClassPath() + "/static/model/LogisticRegressionModel";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/shield/ShieldModel.net");
            modelFile = BasicUtil.streamToFile(is);
        }

        // 创建JavaSparkContent
        JavaSparkContext sc = new JavaSparkContext(sparkSession.sparkContext());
        // 创建一个HashingTF实例来把文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);
        LogisticRegressionModel model;
        if (modelFile.exists()) {
            // 存在即使用
            model = sparkModelDao.getLogisticRegressionModel();
        } else {
            // 不存在重新进行归集训练
            JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc, tf);
            trainData.cache();

            // Run training algorithm to build the model.
            model = new LogisticRegressionWithLBFGS()
                .setNumClasses(2)
                .run(trainData.rdd());

            // 进行模型存储(是否可置于异步来做)
            model.save(sparkSession.sparkContext(), modelPath);
        }

        // 对于单一词的判断限制
        if (Arrays.asList(content.split(" ")).size() < 2) {
            return "pos";
        } else {
            return model.predict(tf.transform(Arrays.asList(content.split(" ")))) == 1.0 ? "neg" : "pos";
        }
    }

}
