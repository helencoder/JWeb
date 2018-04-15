package com.helencoder.dao;

import com.helencoder.domain.utils.WebConstants;
import org.apache.spark.mllib.classification.LogisticRegressionModel;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.tree.model.DecisionTreeModel;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * Spark模型缓存
 *
 * Created by zhenghailun on 2018/3/31.
 */
@Repository
public class SparkModelDao {

    @Autowired
    private transient SparkSession sparkSession;

    /**
     * 缓存配置
     */
    private static final String MODEL_CACHE_NAME = "modelCache";
    private static final String DECISION_TREE_CACHE_KEY = "\"decisionTree\"";
    private static final String LOGISTIC_REGRESSION_CACHE_KEY = "\"logisticRegression\"";
    private static final String NAIVE_BAYES_CACHE_KEY = "\"naiveBayes\"";
    private static final String RANDOM_FOREST_CACHE_KEY = "\"randomForest\"";
    private static final String SVM_CACHE_KEY = "\"svm\"";

    /**
     * 获取决策树模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = DECISION_TREE_CACHE_KEY)
    public DecisionTreeModel getDecisionTreeModel() {
        String modelPath = WebConstants.getClassPath() + "/static/model/DecisionTreeModel";
        return DecisionTreeModel.load(sparkSession.sparkContext(), modelPath);
    }

    /**
     * 获取逻辑回归模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = LOGISTIC_REGRESSION_CACHE_KEY)
    public LogisticRegressionModel getLogisticRegressionModel() {
        String modelPath = WebConstants.getClassPath() + "/static/model/LogisticRegressionModel";
        return LogisticRegressionModel.load(sparkSession.sparkContext(), modelPath);
    }

    /**
     * 获取朴素贝叶斯模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = NAIVE_BAYES_CACHE_KEY)
    public NaiveBayesModel getNaiveBayesModel() {
        String modelPath = WebConstants.getClassPath() + "/static/model/NaiveBayesModel";
        return NaiveBayesModel.load(sparkSession.sparkContext(), modelPath);
    }

    /**
     * 获取随机森林模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = RANDOM_FOREST_CACHE_KEY)
    public RandomForestModel getRandomForestModel() {
        String modelPath = WebConstants.getClassPath() + "/static/model/RandomForestModel";
        return RandomForestModel.load(sparkSession.sparkContext(), modelPath);
    }

    /**
     * 获取SVM模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = SVM_CACHE_KEY)
    public SVMModel getSVMModel() {
        String modelPath = WebConstants.getClassPath() + "/static/model/SVMModel";
        return SVMModel.load(sparkSession.sparkContext(), modelPath);
    }

}
