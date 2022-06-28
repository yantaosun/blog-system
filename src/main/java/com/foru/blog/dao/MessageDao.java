package com.foru.blog.dao;

import com.foru.blog.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 留言
 *
 * @author 9527
 * @date 2021/08/10
 */
@Repository
public interface MessageDao extends BaseMapper<Message> {

}
