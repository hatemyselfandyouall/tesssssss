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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YS-GZD-1495 on 2018/7/27.
 */
@Controller
@RequestMapping("/helloWorld")
public class HelloWorld {
    @RequestMapping(value = "/test.htm",  method = RequestMethod.GET)
    public String test(ModelMap map) {
        List<Blog> blogs=new ArrayList<>();
        for(int i=0;i<5;i++){
            Blog blog=new Blog();
            blog.setName("hello thymeleaf"+i);
            blog.setContent("hello thymeleaf+i");
            blogs.add(blog);
        }
        map.put("blogs",blogs);
        return "helloWorld/helloWorld";
    }

    public static void main(String[] args) {
        System.out.printf(System.getProperty("java.class.path") );
    }
}
