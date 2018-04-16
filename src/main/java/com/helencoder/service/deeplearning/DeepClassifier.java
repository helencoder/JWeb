package com.helencoder.service.deeplearning;

import com.helencoder.dao.DeepModelDao;
import com.helencoder.domain.utils.WebConstants;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于word2vec+lstm的分类器(模型离线处理)
 *
 * Created by zhenghailun on 2018/3/29.
 */
@Component
public class DeepClassifier {
    @Autowired
    private Word2VecModel word2VecModel;
    @Autowired
    private DeepModelDao deepModelDao;

    TokenizerFactory tokenizerFactory;

    public String run(String content) throws Exception {
        // 检查模型文件是否存在
        // 首先进行判断对应模型目录是否存在
        File classPath = new File(WebConstants.getClassPath());
        String modelPath = classPath + "/static/data/deep/emotion/EmotionModel.net";
        File modelFile = new File(modelPath);
        String WORD_VECTORS_PATH = classPath + "/static/data/deep/emotion/dengta.model";
        //wordVectors = word2VecModel.load(WORD_VECTORS_PATH);
        WordVectors wordVectors = deepModelDao.getWordVectors();
        //wordVectors = WordVectorSerializer.readWord2VecModel(new File(WORD_VECTORS_PATH));

        String userDirectory = classPath + "/static/data/deep/emotion/";
        String DATA_PATH = classPath + "/static/data/deep/emotion/LabelledNews";

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        String result = "";
        if (modelFile.exists()) {
            // 直接获取模型进行预测
            //net = ModelSerializer.restoreMultiLayerNetwork(modelPath);
            MultiLayerNetwork net = deepModelDao.getNet();

            boolean flag = isPredict(content, wordVectors, tokenizerFactory);

            if (flag) {
                DataSet testNews = prepareTestData(content, wordVectors, tokenizerFactory);
                INDArray fet = testNews.getFeatureMatrix();
                INDArray predicted = net.output(fet, false);
                int arrsiz[] = predicted.shape();

                File categories = new File(DATA_PATH + File.separator + "categories.txt");

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
            // 模型训练

            int batchSize = 50;     //Number of examples in each minibatch
            int nEpochs = 20;        //Number of epochs (full passes of training data) to train on
            int truncateReviewsToLength = 300;  //Truncate reviews with length (# words) greater than this

            //DataSetIterators for training and testing respectively
            //Using AsyncDataSetIterator to do data loading in a separate thread; this may improve performance vs. waiting for data to load

            NewsIterator iTrain = new NewsIterator.Builder()
                    .dataDirectory(DATA_PATH)
                    .wordVectors(wordVectors)
                    .batchSize(batchSize)
                    .truncateLength(truncateReviewsToLength)
                    .tokenizerFactory(tokenizerFactory)
                    .train(true)
                    .build();

            NewsIterator iTest = new NewsIterator.Builder()
                    .dataDirectory(DATA_PATH)
                    .wordVectors(wordVectors)
                    .batchSize(batchSize)
                    .tokenizerFactory(tokenizerFactory)
                    .truncateLength(truncateReviewsToLength)
                    .train(false)
                    .build();

            //DataSetIterator train = new AsyncDataSetIterator(iTrain,1);
            //DataSetIterator test = new AsyncDataSetIterator(iTest,1);

            int inputNeurons = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length; // 100 in our case
            int outputs = iTrain.getLabels().size();

            tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
            //Set up network configuration
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
                    .updater(Updater.RMSPROP)
                    .regularization(true).l2(1e-5)
                    .weightInit(WeightInit.XAVIER)
                    .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
                    .learningRate(0.0018)
                    .list()
                    .layer(0, new GravesLSTM.Builder().nIn(inputNeurons).nOut(200)
                            .activation(Activation.SOFTSIGN).build())
                    .layer(1, new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                            .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(200).nOut(outputs).build())
                    .pretrain(false).backprop(true).build();

            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            net.setListeners(new ScoreIterationListener(1));

            System.out.println("Starting training");
            for (int i = 0; i < nEpochs; i++) {
                net.fit(iTrain);
                iTrain.reset();
                System.out.println("Epoch " + i + " complete. Starting evaluation:");

                //Run evaluation. This is on 25k reviews, so can take some time
                Evaluation evaluation = net.evaluate(iTest);

                System.out.println(evaluation.stats());
            }

            ModelSerializer.writeModel(net, userDirectory + "EmotionModel.net", true);
            System.out.println("----- Example complete -----");
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
