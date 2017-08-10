package spittr.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


/**
 * Created by helencoder on 2017/8/10.
 */

public class HomeControllerTest {

    @Test
    public void testHomePage() throws Exception {
        HomeController controller = new HomeController();
//        assertEquals("home", controller.home());
        // 搭建MockMVC
        MockMvc mockMvc = standaloneSetup(controller).build();
        // 对“/”执行GET请求，预期得到home视图
        mockMvc.perform(get("/")).andExpect(view().name("home"));
        //mockMvc.perform(get("/homepage")).andExpect(view().name("home"));
    }

}
