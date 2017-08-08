package spittr.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by helencoder on 2017/8/8.
 */

@Controller     // 声明为一个控制器
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)    // 处理对“/”的GET请求
    public String home() {
        return "home";  // 视图名为home
    }
}
