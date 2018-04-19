package com.helencoder.service.deeplearning;

import com.helencoder.dao.DeepModelDao;
import com.helencoder.domain.utils.BasicUtil;
import com.helencoder.domain.utils.WebConstants;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于深度学习的内容审核分类器
 *
 * Created by zhenghailun on 2018/4/16.
 */
@Component
public class ShieldClassifier {
    @Autowired
    private Word2VecModel word2VecModel;
    @Autowired
    private DeepModelDao deepModelDao;

    TokenizerFactory tokenizerFactory;

    public String run(String content) throws Exception {
        // 检查模型文件是否存在
        // 首先进行判断对应模型目录是否存在
        File classPath = new File(WebConstants.getClassPath());
        String modelPath = classPath + "/static/data/deep/shield/ShieldModel.net";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/static/data/deep/shield/ShieldModel.net");
            modelFile = BasicUtil.streamToFile(is);
        }

        WordVectors wordVectors = deepModelDao.getShieldWordVectors();

        String categoryPath = classPath + "/static/data/deep/shield/categories.txt";
        File categories = new File(categoryPath);
        if (!categories.exists()) {
            InputStream cis = this.getClass().getResourceAsStream("/static/data/deep/shield/categories.txt");
            categories = BasicUtil.streamToFile(cis);
        }

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        String result = "";
        if (modelFile.exists()) {
            // 直接获取模型进行预测
            //net = ModelSerializer.restoreMultiLayerNetwork(modelPath);
            MultiLayerNetwork net = deepModelDao.getShieldNet();

            boolean flag = isPredict(content, wordVectors, tokenizerFactory);

            if (flag) {
                DataSet testNews = prepareTestData(content, wordVectors, tokenizerFactory);
                INDArray fet = testNews.getFeatureMatrix();
                INDArray predicted = net.output(fet, false);
                int arrsiz[] = predicted.shape();


                double max = 0;
                int pos = 0;
                for (int i = 0; i < arrsiz[1]; i++) {
                    if (max < (double) predicted.getColumn(i).sumNumber()) {
                        max = (double) predicted.getColumn(i).sumNumber();
                        pos = i;
                    }
                }

                try (BufferedReader brCategories = new BufferedReader(new FileReader(categories))) {
                    String temp = "";
                    List<String> labels = new ArrayList<>();
                    while ((temp = brCategories.readLine()) != null) {
                        labels.add(temp);
                    }
                    brCategories.close();
                    String tag = labels.get(pos).split(",")[1];
                    result = tag;
                } catch (Exception e) {
                    System.out.println("File Exception : " + e.getMessage());
                }
            } else {
                result = "pos";
            }
        } else {
            result = "pos";
        }

        return result;
    }

    /**
     * 数据验证
     */
    private boolean isPredict(String words, WordVectors wordVectors, TokenizerFactory tokenizerFactory) {
        int count = 0;
        List<String> tokens = tokenizerFactory.create(words).getTokens();
        for (String token : tokens) {
            if (wordVectors.hasWord(token)) {
                count++;
            }
        }

        return count > 0;
    }

    /**
     * 预测数据
     */
    private DataSet prepareTestData(String i_news, WordVectors wordVectors, TokenizerFactory tokenizerFactory) {
        List<String> news = new ArrayList<>(1);
        int[] category = new int[1];
        int currCategory = 0;
        news.add(i_news);

        List<List<String>> allTokens = new ArrayList<>(news.size());
        int maxLength = 0;
        for (String s : news) {
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for (String t : tokens) {
                if (wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength, tokensFiltered.size());
        }

        INDArray features = Nd4j.create(news.size(), wordVectors.lookupTable().layerSize(), maxLength);
        INDArray labels = Nd4j.create(news.size(), 3, maxLength);    //labels: Crime, Politics, Bollywood, Business&Development
        INDArray featuresMask = Nd4j.zeros(news.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(news.size(), maxLength);

        int[] temp = new int[2];
        for (int i = 0; i < news.size(); i++) {
            List<String> tokens = allTokens.get(i);
            temp[0] = i;
            for (int j = 0; j < tokens.size() && j < maxLength; j++) {
                String token = tokens.get(j);
                INDArray vector = wordVectors.getWordVectorMatrix(token);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i),
                                NDArrayIndex.all(),
                                NDArrayIndex.point(j)},
                        vector);

                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);
            }
            int idx = category[i];
            int lastIdx = Math.min(tokens.size(), maxLength);
            labels.putScalar(new int[]{i, idx, lastIdx - 1}, 1.0);
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }

        DataSet ds = new DataSet(features, labels, featuresMask, labelsMask);
        return ds;
    }
}
