package com.foru.blog.dto.blog;

import lombok.Data;

import java.util.List;

/**
 * 资源角色
 *
 * @author 9527
 * @date 2021/07/28
 */
@Data
public class ResourceRoleDTO {

    /**
     * 资源id
     */
    private Integer id;

    /**
     * 路径
     */
    private String url;

    /**
     * 请求方式
     */
    private String requestMethod;


    /**
     * 匿名访问
     */
    private Integer isAnonymous;

    /**
     * 角色名
     */
    private List<String> roleList;

}
