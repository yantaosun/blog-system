package com.foru.blog.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交登录token
 *
 * @author 9527
 * @date 2021/07/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialTokenDTO {

    /**
     * 开放id
     */
    private String openId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 登录类型
     */
    private Integer loginType;

}
