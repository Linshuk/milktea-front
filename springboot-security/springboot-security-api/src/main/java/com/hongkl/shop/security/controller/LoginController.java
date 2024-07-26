package com.hongkl.shop.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hongkl.shop.exception.YamiShopBindException;
import com.hongkl.shop.response.ServerResponseEntity;
import com.hongkl.shop.dao.UserMapper;
import com.hongkl.shop.model.User;
import com.hongkl.shop.security.common.bo.UserInfoInTokenBO;
import com.hongkl.shop.security.common.dto.AuthenticationDTO;
import com.hongkl.shop.security.common.enums.SysTypeEnum;
import com.hongkl.shop.security.common.manager.PasswordCheckManager;
import com.hongkl.shop.security.common.manager.PasswordManager;
import com.hongkl.shop.security.common.manager.TokenStore;
import com.hongkl.shop.security.common.vo.TokenInfoVO;
import com.hongkl.shop.utils.PrincipalUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 菠萝凤梨
 * @date 2022/3/28 15:20
 */
@RestController
//@Tag(name = "登录")
public class LoginController {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordCheckManager passwordCheckManager;

    @Autowired
    private PasswordManager passwordManager;

    @PostMapping("/login")
    @Operation(summary = "账号密码(用于前端登录)" , description = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public ServerResponseEntity<TokenInfoVO> login(
            @Valid @RequestBody AuthenticationDTO authenticationDTO) {
        String mobileOrUserName = authenticationDTO.getUserName();
        User user = getUser(mobileOrUserName);

        String decryptPassword = passwordManager.decryptPassword(authenticationDTO.getPassWord());

        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.ORDINARY,authenticationDTO.getUserName(), decryptPassword, user.getLoginPassword());

        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setUserId(user.getUserId());
        userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInToken.setEnabled(user.getStatus() == 1);
        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        return ServerResponseEntity.success(tokenInfoVO);
    }

    private User getUser(String mobileOrUserName) {
        User user = null;
        // 手机验证码登陆，或传过来的账号很像手机号
        if (PrincipalUtil.isMobile(mobileOrUserName)) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobileOrUserName));
        }
        // 如果不是手机验证码登陆， 找不到手机号就找用户名
        if  (user == null) {
            user = userMapper.selectOneByUserName(mobileOrUserName);
        }
        if (user == null) {
            throw new YamiShopBindException("账号或密码不正确");
        }
        return user;
    }
}
