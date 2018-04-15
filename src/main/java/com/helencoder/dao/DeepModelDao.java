package com.helencoder.dao;

import com.helencoder.domain.utils.WebConstants;
import com.helencoder.service.deeplearning.Word2VecModel;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

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

    /**
     * 获取深度模型
     */
    @Cacheable(value = MODEL_CACHE_NAME, key = MODEL_CACHE_KEY)
    public MultiLayerNetwork getNet() throws Exception {
        System.out.println("进入深度模型，执行方法！");
        String modelPath = WebConstants.getClassPath() + "/static/data/deep/emotion/EmotionModel.net";
        return ModelSerializer.restoreMultiLayerNetwork(modelPath);
    }

    /**
     * 获取词向量模型
     */
    @Cacheable(value = VECTOR_CACHE_NAME, key = VECTOR_CACHE_KEY)
    public WordVectors getWordVectors() {
        System.out.println("进入词向量，执行方法！");
        String WORD_VECTORS_PATH = WebConstants.getClassPath() + "/static/data/deep/emotion/dengta.model";
        return word2VecModel.load(WORD_VECTORS_PATH);
    }

}
