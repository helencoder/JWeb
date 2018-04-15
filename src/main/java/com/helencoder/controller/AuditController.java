package com.helencoder.controller;

import com.helencoder.dao.MessageDao;
import com.helencoder.service.ClassificationService;
import com.helencoder.service.FilterService;
import com.helencoder.service.SegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * 审核控制器
 *
 * Created by zhenghailun on 2018/3/19.
 */
@Component("AuditController")
@RestController
public class AuditController {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private SegmentationService segmentationService;

    @Autowired
    private FilterService filterService;

    @RequestMapping("/audit")
    public MessageDao audit(@RequestBody(required = true)Map<String, Object> map) throws Exception {
        long startTime = System.currentTimeMillis();
        String content = map.get("content").toString();
        String source = map.get("source").toString();

        String method = "LogisticRegression";
        if (source.equals("emotion")) {
            method = "Deep";
        }

        // 分词
        String segContent = segmentationService.segWords(content, " ");

        // 敏感词过滤
        String[] words = segContent.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (filterService.check(word)) {
                sb.append(word);
                sb.append(" ");
            }
        }

        MessageDao messageDao = new MessageDao();
        if (sb.toString().length() > 0) {
            messageDao.setCode("100000");
            messageDao.setData("0");
            messageDao.setMsg("文本包含敏感词： " + sb.toString());
        } else {
            // 文本审核
            String res = classificationService.run(segContent, method);
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            String msg = "耗时：" + time + "ms";
            System.out.println(res);
            switch (res) {
                case "pos":
                    messageDao.setData("0");
                    break;
                case "neg":
                    messageDao.setData("1");
                    break;
                default:
                    messageDao.setData("2");
                    break;
            }
            messageDao.setCode("100000");
            messageDao.setMsg(msg);
        }

        return messageDao;
    }

    @RequestMapping("/deep-audit")
    public MessageDao audit() {
        long startTime = System.currentTimeMillis();

        MessageDao messageDao = new MessageDao();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        String msg = "耗时：" + time + "ms";
        messageDao.setCode("100000");
        messageDao.setData("1");
        messageDao.setMsg(msg);

        return messageDao;
    }

}