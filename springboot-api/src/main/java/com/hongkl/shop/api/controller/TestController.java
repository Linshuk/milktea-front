package com.hongkl.shop.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hongkl.shop.exception.YamiShopBindException;
import com.hongkl.shop.model.User;
import com.hongkl.shop.params.UserRegisterParam;
import com.hongkl.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: hongkl
 * @Date: 2024/7/26 00:04
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
public class TestController {
    @Autowired
    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/hello")
    public void hello(){
        System.out.println("hello is ok");

    }


//    @PostMapping("/register")
//    @Operation(summary = "注册" , description = "用户注册或绑定手机号接口")
//    public String register(@Valid @RequestBody UserRegisterParam userRegisterParam) {
//        if (StrUtil.isBlank(userRegisterParam.getNickName())) {
//            userRegisterParam.setNickName(userRegisterParam.getUserName());
//        }
//        // 正在进行申请注册
//        if (userService.count(new LambdaQueryWrapper<User>().eq(User::getNickName, userRegisterParam.getNickName())) > 0) {
//            // 该用户名已注册，无法重新注册
//            throw new YamiShopBindException("该用户名已注册，无法重新注册");
//        }
//
//        return userRegisterParam.getUserName();
//    }


}
