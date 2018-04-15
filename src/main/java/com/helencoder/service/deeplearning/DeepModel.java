package com.helencoder.service.deeplearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 深度模型离线训练部分
 *
 * Created by zhenghailun on 2018/3/29.
 */
@Component
public class DeepModel {
    @Autowired
    private Word2VecModel word2VecModel;

    @Autowired
    private Doc2VecModel doc2VecModel;

    /**
     * word2vec模型训练
     *
     * @param filePath 待训练文本路径
     * @param modelPath  训练模型存储路径
     */
    public void trainWord2VecModel(String filePath, String modelPath) throws Exception {
        word2VecModel.train(filePath, modelPath);
    }

    /**
     * doc2vec模型训练
     *
     * @param filePath 待训练文本路径
     * @param modelPath  训练模型存储路径
     */
    public void trainDoc2VecModel(String filePath, String modelPath) throws Exception {
        doc2VecModel.train(filePath, modelPath);
    }

}
