package com.foru.blog.config.handler;

import com.alibaba.fastjson.JSON;
import com.foru.blog.annotation.LoginLog;
import com.foru.blog.constant.CommonConst;
import com.foru.blog.constant.OptTypeConst;
import com.foru.blog.dao.UserAuthDao;
import com.foru.blog.dto.blog.UserInfoDTO;
import com.foru.blog.util.BeanCopyUtils;
import com.foru.blog.util.UserUtils;
import com.foru.blog.vo.Result;
import com.foru.blog.entity.UserAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 登录成功处理
 *
 * @author 9527
 * @date 2021/07/28
 */
@Component
@Api(tags = "用户登录模块")
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    private UserAuthDao userAuthDao;

    @Override
    @LoginLog(optType = OptTypeConst.LOGIN)
    @ApiOperation("用户登录")
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // 返回登录信息
        UserInfoDTO userLoginDTO = BeanCopyUtils.copyObject(UserUtils.getLoginUser(), UserInfoDTO.class);
        httpServletResponse.setContentType(CommonConst.APPLICATION_JSON);
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.ok(userLoginDTO)));
        // 更新用户ip，最近登录时间
        updateUserInfo();
    }

    /**
     * 更新用户信息
     */
    @Async
    public void updateUserInfo() {
        UserAuth userAuth = UserAuth.builder()
                .id(UserUtils.getLoginUser().getId())
                .ipAddress(UserUtils.getLoginUser().getIpAddress())
                .ipSource(UserUtils.getLoginUser().getIpSource())
                .lastLoginTime(UserUtils.getLoginUser().getLastLoginTime())
                .build();
        userAuthDao.updateById(userAuth);
    }

}
