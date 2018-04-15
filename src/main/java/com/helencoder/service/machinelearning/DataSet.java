package com.helencoder.service.machinelearning;

import com.helencoder.domain.utils.WebConstants;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.regression.LabeledPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * spark分类数据
 *
 * Created by helencoder on 2018/3/5.
 */
public class DataSet {

    /**
     * 训练数据加载
     */
    public static JavaRDD<LabeledPoint> loadTrainSet(JavaSparkContext sc, HashingTF tf) throws FileNotFoundException {
        // 加载训练数据
        // 首先进行判断对应模型目录是否存在
        File classPath = new File(WebConstants.getClassPath());
        String dataPath = classPath + "/static/data/classification";
        String posFilePath = dataPath + "/pos.txt";
        String negFilePath = dataPath + "/neg.txt";
        JavaRDD<String> posData = sc.textFile(posFilePath);
        JavaRDD<String> negData = sc.textFile(negFilePath);

        // 训练数据向量化
        JavaRDD<LabeledPoint> posRDD = posData.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(0, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );
        JavaRDD<LabeledPoint> negRDD = negData.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String file) throws Exception {
                        return new LabeledPoint(1, tf.transform(Arrays.asList(file.split(" "))));
                    }
                }
        );

        JavaRDD<LabeledPoint> trainData = posRDD.union(negRDD);
        trainData.cache();  // 缓存训练数据RDD

        return trainData;
    }

    /**
     * 测试数据加载
     */
    public static void loadTestSet(JavaSparkContext sc) {
        String testFilePath = "data/classification/test.txt";

    }


}
