package spittr.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by helencoder on 2017/8/8.
 */

@Controller     // 声明为一个控制器
@RequestMapping({"/", "/homepage"})    // 将控制器映射到“/”,“/homepage”
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)    // 处理GET请求
    public String home() {
        return "home";  // 视图名为home
    }
}
