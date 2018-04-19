package com.helencoder.service.machinelearning;

import com.helencoder.dao.SparkModelDao;
import com.helencoder.domain.utils.BasicUtil;
import com.helencoder.domain.utils.WebConstants;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * NaiveBayes
 *
 * Created by zhenghailun on 2018/3/26.
 */
@Component
public class NaiveBayesClassifier  implements Serializable {
    @Autowired
    private transient SparkSession sparkSession;

    @Autowired
    private transient SparkModelDao sparkModelDao;

    public String run(String content) throws FileNotFoundException {

        // 首先进行判断对应模型目录是否存在
        String modelPath = WebConstants.getClassPath() + "/static/model/NaiveBayesModel";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/model/NaiveBayesModel");
            modelFile = BasicUtil.streamToFile(is);
        }

        // 创建JavaSparkContent
        JavaSparkContext sc = new JavaSparkContext(sparkSession.sparkContext());
        // 创建一个HashingTF实例来把文本映射为包含10000个特征的向量
        final HashingTF tf = new HashingTF(10000);
        NaiveBayesModel model;
        if (modelFile.exists()) {
            // 存在即使用
            model = sparkModelDao.getNaiveBayesModel();
        } else {
            // 不存在重新进行归集训练
            JavaRDD<LabeledPoint> trainData = DataSet.loadTrainSet(sc, tf);
            trainData.cache();

            // Run training algorithm to build the model.
            model = NaiveBayes.train(trainData.rdd(), 1.0);

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
