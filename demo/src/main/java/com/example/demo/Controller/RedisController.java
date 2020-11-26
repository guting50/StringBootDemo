package com.example.demo.Controller;

import com.gt.core.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("redis")
@RestController
@Api(tags = "redis测试")
public class RedisController {

    @Autowired
    RedisUtil redisUtil;

    @GetMapping("/set")
    @ApiOperation(value = "redis保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "键", required = true, defaultValue = "aaa"),
            @ApiImplicitParam(name = "value", value = "值", required = true, defaultValue = "123456")
    })
    public String set(String key, String value) {
        if (redisUtil.set(key, value)) {
            return "成功";
        }
        return "失败";
    }

    @GetMapping("/get")
    @ApiOperation(value = "redis获取")
    @ApiImplicitParam(name = "key", value = "键", required = true, defaultValue = "aaa")
    public Object set(String key) {
        return redisUtil.get(key);
    }
}
