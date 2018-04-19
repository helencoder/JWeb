package com.helencoder.controller;

import com.helencoder.dao.DataBaseDao;
import com.helencoder.dao.MessageDao;
import com.helencoder.domain.entity.CommunityEventsVo;
import com.helencoder.domain.entity.CommunityShieldRecordsVo;
import com.helencoder.service.ClassificationService;
import com.helencoder.service.FilterService;
import com.helencoder.service.RecordService;
import com.helencoder.service.SegmentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    private Environment env;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private SegmentationService segmentationService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private RecordService recordService;

    @RequestMapping("/audit")
    public MessageDao audit(@RequestBody(required = true)Map<String, Object> map) throws Exception {
        long startTime = System.currentTimeMillis();
        String content = map.get("content").toString();
        String source = map.get("source").toString();
        String requestIp = request.getRemoteAddr();

        // 用户访问数据记录
        CommunityEventsVo communityEventsVo = new CommunityEventsVo();
        communityEventsVo.setUserId(requestIp);
        communityEventsVo.setRequestData(content);
        communityEventsVo.setRequestTime();
        recordService.recordCommunityEvent(communityEventsVo);

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
            messageDao.setData("1");
            messageDao.setMsg("文本包含敏感词： " + sb.toString());
        } else {
            // 文本审核(机器学习、深度学习同时判断)
            String mlRes = classificationService.run(segContent, "LogisticRegression");
            String dlRes = classificationService.run(segContent, "Deep");
            System.out.println("机器识别结果" + mlRes + "\t深度识别结果：" + dlRes);
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            String msg = "耗时: " + time + "ms" + "\t"
                    + "机器识别结果: " + getRes(mlRes) + "\t"
                    + "深度识别结果: " + getRes(dlRes);

            String res = "";
            if (mlRes.equals("pos") && dlRes.equals("pos")) {
                res = "pos";
            } else if ((mlRes.equals("pos") && dlRes.equals("neg")) || (mlRes.equals("neg") && dlRes.equals("pos"))) {
                res = "nor";
            } else {
                res = "neg";
            }

            switch (res) {
                case "pos":
                    messageDao.setData("0");
                    break;
                case "neg":
                    messageDao.setData("1");
                    break;
                case "nor":
                default:
                    messageDao.setData("2");
                    break;
            }
            messageDao.setCode("100000");
            messageDao.setMsg(msg);

            // 审核数据记录
            CommunityShieldRecordsVo communityShieldRecordsVo = new CommunityShieldRecordsVo();
            communityShieldRecordsVo.setData(content);
            communityShieldRecordsVo.setMlRes(mlRes);
            communityShieldRecordsVo.setDlRes(dlRes);
            communityShieldRecordsVo.setFinalRes(res);
            communityShieldRecordsVo.setCreateTime();
            communityShieldRecordsVo.setUpdateTime();
            recordService.recordCommunityShieldRecord(communityShieldRecordsVo);

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

    private String getRes(String str) {
        String label = "";
        switch (str) {
            case "nor":
                label = "疑似";
                break;
            case "neg":
                label = "违规";
                break;
            case "pos":
            default:
                label = "通过";
                break;
        }
        return label;
    }

}
