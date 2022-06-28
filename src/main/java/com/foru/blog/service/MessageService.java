package com.foru.blog.service;

import com.foru.blog.dto.blog.MessageBackDTO;
import com.foru.blog.dto.blog.MessageDTO;
import com.foru.blog.entity.Message;
import com.foru.blog.vo.ConditionVO;
import com.foru.blog.vo.MessageVO;
import com.foru.blog.vo.PageResult;
import com.foru.blog.vo.ReviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 留言服务
 *
 * @author 9527
 * @date 2021/07/29
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加留言弹幕
     *
     * @param messageVO 留言对象
     */
    void saveMessage(MessageVO messageVO);

    /**
     * 查看留言弹幕
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 审核留言
     *
     * @param reviewVO 审查签证官
     */
    void updateMessagesReview(ReviewVO reviewVO);

    /**
     * 查看后台留言
     *
     * @param condition 条件
     * @return 留言列表
     */
    PageResult<MessageBackDTO> listMessageBackDTO(ConditionVO condition);

}
