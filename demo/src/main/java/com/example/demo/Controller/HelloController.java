package com.example.demo.Controller;

import com.example.demo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "测试")
public class HelloController {

    @Autowired
    UserService userService;

    @GetMapping("/hello")
    @ApiOperation(value = "测试", notes = "测试Spring Boot")
    public String hello() {
        return "Hello Spring Boot!" + userService.selectByPrimaryKey(1).toString();
    }
}
