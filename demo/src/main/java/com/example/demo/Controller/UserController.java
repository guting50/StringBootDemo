package com.example.demo.Controller;

import com.alibaba.druid.util.StringUtils;
import com.example.demo.service.UserService;
import com.example.demo.vo.User;
import com.gt.core.utils.FileUtilTool;
import com.gt.core.utils.SessionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;


@RequestMapping("user")
@RestController
@Api(tags = "用户相关接口")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "/login")
    @ApiOperation(value = "登录", notes = "通过username、password操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, defaultValue = "admin"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, defaultValue = "123456")
    })
    public String login(String username, String password,
                        HttpServletRequest req, HttpServletResponse reps) {
        User user = userService.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            SessionUtils.save(req, reps, user);
            return "登录成功";
        } else
            return "登录失败";
    }

    @GetMapping("/getUser")
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, defaultValue = "admin")
    })
    public User getUser(String username) {
        return userService.selectByUsername(username);
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册", notes = "用户注册")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "user", value = "用户对象", required = true, dataType = "User.class")
    })
    public String register(@RequestBody User user) {
        if (userService.insertSelective(user) > 0) {
            return "成功";
        }
        return "失败";
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传文件", notes = "测试上传文件")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "img", value = "用户头像图片", required = true, dataType = "__file")
    })
    public String uploadFile(String username, @RequestParam(value = "img") MultipartFile uploadImage) {
        User user = userService.selectByUsername(username);
        String filePath = FileUtilTool.ensureFileDirectory(FileUtilTool.head_img_file);
        InputStream inputStream = null;
        try {
            inputStream = uploadImage.getInputStream();
            FileUtilTool.saveToDisk(inputStream, filePath, FileUtilTool.userHeadFileName);
            if (!StringUtils.isEmpty(user.getAvatar())) {
                File file = new File(user.getAvatar());
                if (file.exists() && file.isDirectory()) {
                    FileUtilTool.removeFileOrDirectory(user.getAvatar(), user.getAvatar() + FileUtilTool.userHeadFileName);
                }
            }
            user.setAvatar(filePath.substring(10));//截图路径
            if (userService.updateByPrimaryKeySelective(user) > 0) {
                return "成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "失败";
    }


    @GetMapping("/hpjj")
    public String hpjj() {
        return "hpjj";
    }
}
