package com.foru.blog.controller;


import com.alibaba.fastjson.JSONObject;
import com.foru.blog.annotation.AccessLimit;
import com.foru.blog.vo.*;
import com.foru.blog.dto.blog.UserAreaDTO;
import com.foru.blog.dto.blog.UserInfoDTO;
import com.foru.blog.dto.blog.UserBackDTO;
import com.foru.blog.service.UserAuthService;
import com.foru.blog.vo.gitee.GiteeLoginVO;
import com.foru.blog.vo.qq.QQLoginVO;
import com.foru.blog.vo.weibo.WeiboLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户账号控制器
 *
 * @author 9527
 * @date 2021/07/28
 */
@Api(tags = "用户账号模块")
@RestController
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;

    private Logger logger = LoggerFactory.getLogger(UserAuthController.class);

    /**
     * 发送邮箱验证码
     *
     * @param username 用户名
     * @return {@link Result<>}
     */
    @AccessLimit(seconds = 60, maxCount = 1)
    @ApiOperation(value = "发送邮箱验证码")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String")
    @GetMapping("/users/code")
    public Result<?> sendCode(String username, Integer operation) {
        userAuthService.sendCode(username, operation);
        return Result.ok();
    }

    /**
     * 获取用户区域分布
     *
     * @param conditionVO 条件
     * @return {@link Result<UserAreaDTO>} 用户区域分布
     */
    @ApiOperation(value = "获取用户区域分布")
    @GetMapping("/admin/users/area")
    public Result<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO) {
        return Result.ok(userAuthService.listUserAreas(conditionVO));
    }

    /**
     * 查询后台用户列表
     *
     * @param condition 条件
     * @return {@link Result<UserBackDTO>} 用户列表
     */
    @ApiOperation(value = "查询后台用户列表")
    @GetMapping("/admin/users")
    public Result<PageResult<UserBackDTO>> listUsers(ConditionVO condition) {
        return Result.ok(userAuthService.listUserBackDTO(condition));
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return {@link Result<>}
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserVO user) {
        userAuthService.register(user);
        return Result.ok();
    }

    /**
     * 修改密码
     *
     * @param user 用户信息
     * @return {@link Result<>}
     */
    @ApiOperation(value = "修改密码")
    @PutMapping("/users/password")
    public Result<?> updatePassword(@Valid @RequestBody UserVO user) {
        userAuthService.updatePassword(user);
        return Result.ok();
    }

    /**
     * 修改管理员密码
     *
     * @param passwordVO 密码信息
     * @return {@link Result<>}
     */
    @ApiOperation(value = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public Result<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return Result.ok();
    }

    /**
     * 微博登录
     *
     * @param weiBoLoginVO 微博登录信息
     * @return {@link Result<UserInfoDTO>} 用户信息
     */
    @ApiOperation(value = "微博登录")
    @PostMapping("/users/oauth/weibo")
    public Result<UserInfoDTO> weiboLogin(@Valid @RequestBody WeiboLoginVO weiBoLoginVO) {
        return Result.ok(userAuthService.weiboLogin(weiBoLoginVO));
    }

    /**
     * qq登录
     *
     * @param qqLoginVO qq登录信息
     * @return {@link Result<UserInfoDTO>} 用户信息
     */
    @ApiOperation(value = "qq登录")
    @PostMapping("/users/oauth/qq")
    public Result<UserInfoDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        logger.info("qqLogin receive data is: {}", JSONObject.toJSONString(qqLoginVO));
        return Result.ok(userAuthService.qqLogin(qqLoginVO));
    }


    /**
     * Gitee登录
     *
     * @param giteeLoginVO qq登录信息
     * @return {@link Result<UserInfoDTO>} 用户信息
     */
    @ApiOperation(value = "gitee登录")
    @PostMapping("/users/oauth/gitee")
    public Result<UserInfoDTO> giteeLogin(@Valid @RequestBody GiteeLoginVO giteeLoginVO) {
        logger.info("giteeLogin receive data is: {}", JSONObject.toJSONString(giteeLoginVO));
        return Result.ok(userAuthService.giteeLogin(giteeLoginVO));
    }


    /**
     * GitHub登录
     *
     * @param giteeLoginVO qq登录信息
     * @return {@link Result<UserInfoDTO>} 用户信息
     */
    @ApiOperation(value = "github登录")
    @PostMapping("/users/oauth/github")
    public Result<UserInfoDTO> gitHubLogin(@Valid @RequestBody GiteeLoginVO giteeLoginVO) {
        logger.info("gitHubLogin receive data is: {}", JSONObject.toJSONString(giteeLoginVO));
        return Result.ok(userAuthService.githubLogin(giteeLoginVO));
    }

}

