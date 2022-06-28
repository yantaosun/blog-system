package com.foru.blog.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交账号信息
 *
 * @author 9527
 * @date 2021/07/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialUserInfoDTO {

    /**
     * QQ：OpenId  Gitee:ID
     */
    private String thirdId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;


    /**
     * 邮箱
     */
    private String email;

}
