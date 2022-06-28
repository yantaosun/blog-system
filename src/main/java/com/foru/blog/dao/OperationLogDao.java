package com.foru.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foru.blog.entity.OperationLog;
import org.springframework.stereotype.Repository;


/**
 * 操作日志
 *
 * @author 9527
 * @date 2021/08/10
 */
@Repository
public interface OperationLogDao extends BaseMapper<OperationLog> {
}
