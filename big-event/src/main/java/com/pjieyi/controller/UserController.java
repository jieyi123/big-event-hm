package com.pjieyi.controller;


import com.pjieyi.pojo.Result;
import com.pjieyi.pojo.User;
import com.pjieyi.service.UserService;
import com.pjieyi.utils.JwtUtil;
import com.pjieyi.utils.Md5Util;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author pjieyi
 * @Description 用户相关接口
 */
@RequestMapping("/user")
@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}") String username, @Pattern(regexp = "^\\S{5,16}") String password){
        //  /S{5,16}正则表达式 匹配不为空的5-16个字符
        User user = userService.findByUsername(username);
        if (user == null){
            //用户不存在 新增
            userService.addUser(username,password);
            return Result.success();
        }else {
            return Result.error("用户名已存在");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}") String username, @Pattern(regexp = "^\\S{5,16}") String password){
        //根据用户名查询用户
        User user = userService.findByUsername(username);
        if (user ==null ){
            //用户名不存在
            return Result.error("用户名错误");
        }
        //用户名存在
        if (Md5Util.getMD5String(password).equals(user.getPassword())) {
            //密码正确 返回jwt令牌
            Map<String,Object> map=new HashMap<>();
            map.put("username",user.getUsername());
            map.put("id",user.getId());
            String token = JwtUtil.genToken(map);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

}