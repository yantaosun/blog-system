package com.foru.blog.util;

import com.foru.blog.dto.blog.UserDetailDTO;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * 用户工具类
 *
 * @author 9527
 * @date 2021/08/10
 */
@Component
public class UserUtils {

    /**
     * 获取当前登录用户
     *
     * @return 用户登录信息
     */
    public static UserDetailDTO getLoginUser() {
        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))
            return (UserDetailDTO)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        else
            return null;
    }

}
