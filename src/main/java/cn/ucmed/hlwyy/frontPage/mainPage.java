package cn.ucmed.hlwyy.frontPage;

import cn.ucmed.hlwyy.Model.Blog;
import cn.ucmed.hlwyy.common.ApiResponse;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YS-GZD-1495 on 2018/7/27.
 */
@Controller
@RequestMapping("/frontPage")
public class mainPage {
    @RequestMapping(value = "/index.htm",  method = RequestMethod.GET)
    public String mainPage(ModelMap map) {
        List<Blog> blogs=new ArrayList<>();
        for(int i=0;i<5;i++){
            Blog blog=new Blog();
            blog.setName("hello thymeleaf"+i);
            blog.setContent("hello thymeleaf+i");
        }
        map.put("blogs",blogs);
        return "frontPage/index";
    }
}
