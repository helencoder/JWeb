package com.helencoder.service;

import com.helencoder.service.deeplearning.DeepClassifier;
import com.helencoder.service.machinelearning.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.Serializable;

/**
 * 分类服务(调度各种分类器)
 *
 * Created by zhenghailun on 2018/3/18.
 */
@Component
public class ClassificationService implements Serializable {

    @Autowired
    private LogisticRegressionClassifier logisticRegressionClassifier;
    @Autowired
    private DecisionTreeClassifier decisionTreeClassifier;
    @Autowired
    private NaiveBayesClassifier naiveBayesClassifier;
    @Autowired
    private RandomForestClassifier randomForestClassifier;
    @Autowired
    private SVMClassifier svmClassifier;
    @Autowired
    private DeepClassifier deepClassifier;

    @Value("${model.method}")
    private String modelMethod;

    @Value("${model.path}")
    private String modelPath;

    @Value("${model.data}")
    private String modelDataPath;

    public String run(String content, String method) throws Exception {
        switch (method) {
            case "LogisticRegression":
                return logisticRegressionClassifier.run(content);
            case "DecisionTree":
                return decisionTreeClassifier.run(content);
            case "NaiveBayes":
                return naiveBayesClassifier.run(content);
            case "RandomForest":
                return randomForestClassifier.run(content);
            case "SVM":
                return svmClassifier.run(content);
            case "Deep":
                return deepClassifier.run(content);
            default:
                return naiveBayesClassifier.run(content);
        }
    }
}
