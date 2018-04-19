package com.helencoder.dao;

import com.helencoder.domain.utils.BasicUtil;
import com.helencoder.domain.utils.WebConstants;
import com.helencoder.service.deeplearning.Word2VecModel;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * 加载模型
 *
 * Created by zhenghailun on 2018/3/30.
 */
@Repository
public class DeepModelDao {
    @Autowired
    private Word2VecModel word2VecModel;
    /**
     * 缓存配置
     */
    public static final String MODEL_CACHE_NAME = "modelCache";
    public static final String MODEL_CACHE_KEY = "\"deepNet\"";
    public static final String VECTOR_CACHE_NAME = "vectorCache";
    public static final String VECTOR_CACHE_KEY = "\"wordVector\"";
    public static final String SHIELD_MODEL_CACHE_NAME = "shieldModelCache";
    public static final String SHIELD_MODEL_CACHE_KEY = "\"deepShieldNet\"";
    public static final String SHIELD_VECTOR_CACHE_NAME = "shieldVectorCache";
    public static final String SHIELD_VECTOR_CACHE_KEY = "\"shieldWordVector\"";

    /**
     * 获取深度模型(情感识别)
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = MODEL_CACHE_KEY)
    public MultiLayerNetwork getNet() throws Exception {
        System.out.println("进入深度模型，执行方法！");
        String modelPath = WebConstants.getClassPath() + "/static/data/deep/emotion/EmotionModel.net";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/emotion/EmotionModel.net");
            return ModelSerializer.restoreMultiLayerNetwork(is);
        } else {
            return ModelSerializer.restoreMultiLayerNetwork(modelPath);
        }
    }

    /**
     * 获取词向量模型(情感识别)
     */
    @Cacheable(value = VECTOR_CACHE_NAME, key = VECTOR_CACHE_KEY)
    public WordVectors getWordVectors() {
        System.out.println("进入词向量，执行方法！");
        String WORD_VECTORS_PATH = WebConstants.getClassPath() + "/static/data/deep/emotion/dengta.model";
        File modelFile = new File(WORD_VECTORS_PATH);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/emotion/dengta.model");
            modelFile = BasicUtil.streamToFile(is);
            return WordVectorSerializer.readWord2VecModel(modelFile);
        } else {
            return word2VecModel.load(WORD_VECTORS_PATH);
        }
    }

    /**
     * 获取深度模型(内容审核)
     */
    @Cacheable(value = SHIELD_MODEL_CACHE_NAME, key = SHIELD_MODEL_CACHE_KEY)
    public MultiLayerNetwork getShieldNet() throws Exception {
        System.out.println("进入深度模型，执行方法！");
        String modelPath = WebConstants.getClassPath() + "/static/data/deep/shield/ShieldModel.net";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/shield/ShieldModel.net");
            return ModelSerializer.restoreMultiLayerNetwork(is);
        } else {
            return ModelSerializer.restoreMultiLayerNetwork(modelPath);
        }
    }

    /**
     * 获取词向量模型(内容审核)
     */
    @Cacheable(value = SHIELD_VECTOR_CACHE_NAME, key = SHIELD_VECTOR_CACHE_KEY)
    public WordVectors getShieldWordVectors() {
        System.out.println("进入词向量，执行方法！");
        String WORD_VECTORS_PATH = WebConstants.getClassPath() + "/static/data/deep/shield/shieldWordVector.model";
        File modelFile = new File(WORD_VECTORS_PATH);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/shield/shieldWordVector.model");
            modelFile = BasicUtil.streamToFile(is);
            return WordVectorSerializer.readWord2VecModel(modelFile);
        } else {
            return word2VecModel.load(WORD_VECTORS_PATH);
        }
    }

}
