package cn.ucmed.hlwyy.frontPage;

import cn.ucmed.hlwyy.common.ApiResponse;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by YS-GZD-1495 on 2018/7/27.
 */
@RestController
@RequestMapping("/helloWorld")
public class HelloWorld {
    @RequestMapping(value = "/test", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> test() {
        return ApiResponse.responseSuccess("123123");
    }
}
