package com.helencoder.controller;

import com.helencoder.dao.MessageDao;
import com.helencoder.domain.utils.WebConstants;
import com.helencoder.service.deeplearning.DeepModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;

/**
 * 模型训练控制器
 *
 * Created by zhenghailun on 2018/3/29.
 */
@Component("TrainController")
@RestController
public class TrainController {
    @Autowired
    private DeepModel deepModel;

    @RequestMapping("/deep-train")
    public MessageDao train(){
        MessageDao messageDao = new MessageDao();
        try {
            // 首先进行判断对应模型目录是否存在
            File classPath = new File(WebConstants.getClassPath());
            String filePath = classPath + "/static/data/deep/emotion/dengta_seg.txt";
            String modelPath = classPath + "/static/data/deep/emotion/dengta.model";
            deepModel.trainWord2VecModel(filePath, modelPath);

            messageDao.setCode("100000");
            messageDao.setData("1");
            messageDao.setMsg("训练完成");
        } catch (Exception ex) {
            ex.printStackTrace();
            messageDao.setCode("100001");
            messageDao.setData("1");
            messageDao.setMsg("训练异常");
        }

        return messageDao;
    }
}
