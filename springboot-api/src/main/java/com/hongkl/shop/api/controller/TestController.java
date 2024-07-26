package com.hongkl.shop.api.controller;

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

    @RequestMapping("/hello")
    public void hello(){
        System.out.println("hello is ok");

    }


}
