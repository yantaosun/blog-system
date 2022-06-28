package com.foru.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.foru.blog.dto.blog.OperationLogDTO;
import com.foru.blog.entity.OperationLog;
import com.foru.blog.vo.ConditionVO;
import com.foru.blog.vo.PageResult;

/**
 * 操作日志服务
 *
 * @author 9527
 * @date 2021/07/29
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param conditionVO 条件
     * @return 日志列表
     */
    PageResult<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
