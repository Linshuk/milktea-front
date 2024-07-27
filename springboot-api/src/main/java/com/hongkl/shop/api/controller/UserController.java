package com.hongkl.shop.api.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hongkl.shop.exception.YamiShopBindException;
import com.hongkl.shop.model.User;
import com.hongkl.shop.params.UserRegisterParam;
import com.hongkl.shop.response.ServerResponseEntity;
import com.hongkl.shop.security.common.bo.UserInfoInTokenBO;
import com.hongkl.shop.security.common.enums.SysTypeEnum;
import com.hongkl.shop.security.common.manager.PasswordManager;
import com.hongkl.shop.security.common.manager.TokenStore;
import com.hongkl.shop.security.common.vo.TokenInfoVO;
import com.hongkl.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author: hongkl
 * @Date: 2024/7/26 14:45
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final TokenStore tokenStore;

    private final PasswordManager passwordManager;



    @PostMapping("/register")
    @Operation(summary = "注册" , description = "用户注册或绑定手机号接口")
    public ServerResponseEntity<TokenInfoVO> register(@Valid @RequestBody UserRegisterParam userRegisterParam) {
        if (StrUtil.isBlank(userRegisterParam.getNickName())) {
            userRegisterParam.setNickName(userRegisterParam.getUserName());
        }
        // 正在进行申请注册
        if (userService.count(new LambdaQueryWrapper<User>().eq(User::getNickName, userRegisterParam.getNickName())) > 0) {
            // 该用户名已注册，无法重新注册
            throw new YamiShopBindException("该用户名已注册，无法重新注册");
        }
        Date now = new Date();
        User user = new User();
        user.setModifyTime(now);
        user.setUserRegtime(now);
        user.setStatus(1);
        user.setNickName(userRegisterParam.getNickName());
        user.setUserMail(userRegisterParam.getUserMail());
        String decryptPassword = passwordManager.decryptPassword(userRegisterParam.getPassWord());
        user.setLoginPassword(passwordEncoder.encode(decryptPassword));
        String userId = IdUtil.simpleUUID();
        user.setUserId(userId);
        userService.save(user);
        // 2. 登录
        UserInfoInTokenBO userInfoInTokenBO = new UserInfoInTokenBO();
        userInfoInTokenBO.setUserId(user.getUserId());
        userInfoInTokenBO.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInTokenBO.setIsAdmin(0);
        userInfoInTokenBO.setEnabled(true);
        return ServerResponseEntity.success(tokenStore.storeAndGetVo(userInfoInTokenBO));
    }


    @PutMapping("/updatePwd")
    public ServerResponseEntity<Void> updatePwd(@Valid @RequestBody UserRegisterParam param){
        // 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getNickName, param.getNickName()));
        if (ObjectUtil.isEmpty(user)) throw new YamiShopBindException("无法获取该用户信息");

        String decryptPassword = passwordManager.decryptPassword(param.getPassWord());
        if (StrUtil.isBlank(decryptPassword)) throw new YamiShopBindException("新密码不能为空");

        String newPwd = passwordEncoder.encode(decryptPassword);
        if (StrUtil.equals(newPwd,user.getLoginPassword())) throw new YamiShopBindException("新旧密码不能相同");

        user.setModifyTime(new Date());
        user.setLoginPassword(newPwd);
        userService.updateById(user);
        return ServerResponseEntity.success();


    }

}
