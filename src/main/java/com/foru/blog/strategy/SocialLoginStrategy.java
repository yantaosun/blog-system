package com.foru.blog.strategy;

import com.foru.blog.dto.blog.UserInfoDTO;

/**
 * 第三方登录策略
 *
 * @author 9527
 * @date 2021/07/28
 */
public interface SocialLoginStrategy {

    /**
     * 登录
     *
     * @param data 数据
     * @return {@link UserInfoDTO} 用户信息
     */
    UserInfoDTO login(String data);

}
