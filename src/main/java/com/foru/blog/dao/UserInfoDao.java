package com.foru.blog.dao;

import com.foru.blog.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 用户信息
 *
 * @author 9527
 * @date 2021/08/10
 */
@Repository
public interface UserInfoDao extends BaseMapper<UserInfo> {

}
