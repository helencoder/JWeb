package com.helencoder.service.deeplearning;

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
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

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

    /**
     * 深度模型训练
     *
     * @param filePath 待训练文本路径
     * @param modelPath  训练模型存储路径
     */
    public void trainDeepModel(String filePath, String vectorPath, String modelPath,
                               int batchSize, int nEpochs, int truncateReviewsToLength) throws Exception {
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        WordVectors wordVectors = word2VecModel.load(vectorPath);

        // 模型训练
//        int batchSize = 50;     //Number of examples in each minibatch
//        int nEpochs = 20;        //Number of epochs (full passes of training data) to train on
//        int truncateReviewsToLength = 300;  //Truncate reviews with length (# words) greater than this

        //DataSetIterators for training and testing respectively
        //Using AsyncDataSetIterator to do data loading in a separate thread; this may improve performance vs. waiting for data to load

        NewsIterator iTrain = new NewsIterator.Builder()
                .dataDirectory(filePath)
                .wordVectors(wordVectors)
                .batchSize(batchSize)
                .truncateLength(truncateReviewsToLength)
                .tokenizerFactory(tokenizerFactory)
                .train(true)
                .build();

        NewsIterator iTest = new NewsIterator.Builder()
                .dataDirectory(filePath)
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
//            Evaluation evaluation = net.evaluate(iTest);
//            System.out.println(evaluation.stats());

            ModelSerializer.writeModel(net, modelPath, true);
        }

        //ModelSerializer.writeModel(net, modelPath, true);
        System.out.println("----- Example complete -----");
    }


}
