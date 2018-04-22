package com.helencoder.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;

/**
 * 可视化控制器
 *
 * Created by zhenghailun on 2018/4/16.
 */
@Component("VisualizationController")
@Controller
@RequestMapping("/visualization")
public class VisualizationController {

    @RequestMapping(method = RequestMethod.GET)
    public String home(@RequestParam(value = "text", defaultValue = "") String text, Model model) throws FileNotFoundException {
        // 视图渲染
        if (text.isEmpty()) {
            model.addAttribute("msg", "请输入待检测文本");
        } else {
            model.addAttribute("msg", "待检测文本为：" + text);
        }

        return "wordCloud";
    }

    @RequestMapping(path = "/keywords", method = RequestMethod.GET)
    public String keywords(@RequestParam(value = "text", defaultValue = "") String text, Model model) {
        // 视图渲染
        if (text.isEmpty()) {
            model.addAttribute("msg", "请输入待检测文本");
        } else {
            model.addAttribute("msg", "待检测文本为：" + text);
        }

        return "keywordsCloud";
    }

}
