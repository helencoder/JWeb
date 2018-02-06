package com.helencoder.keywords;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 关键词提取
 *
 * Created by zhenghailun on 2018/2/6.
 */
@Component("keywordsController")
@Controller
@RequestMapping("/keywords")
public class HomeController {
    @RequestMapping(method = RequestMethod.GET)
    public String home(@RequestParam(value = "text", defaultValue = "") String text, Model model) {
        // 视图渲染
        if (text.isEmpty()) {
            model.addAttribute("msg", "请输入待检测文本");
        } else {
            model.addAttribute("msg", "待检测文本为：" + text);
        }



        return "keywords";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String search(@RequestParam(value = "text", defaultValue = "") String text, Model model) {
        long startTime = System.currentTimeMillis();
        // 视图渲染
        if (text.isEmpty()) {
            model.addAttribute("msg", "请输入待检测文本");
        } else {
            model.addAttribute("msg", "待检测文本为：" + text.substring(0,10) + "...");
            // 中间逻辑处理
            // 特定敏感词检测

            TextrankOptimization textrankOptimization = new TextrankOptimization();
            textrankOptimization.analyze(text, 5);

            List<String> keywordsList = textrankOptimization.getKeywordsList(10);
            model.addAttribute("keywordWords", keywordsList);
        }
        long endTime = System.currentTimeMillis();
        model.addAttribute("time", "查询用时：" + (endTime - startTime) + "ms");

        return "keywords_res";
    }
}
