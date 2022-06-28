package com.foru.blog.service;

import com.foru.blog.dto.blog.FriendLinkBackDTO;
import com.foru.blog.dto.blog.FriendLinkDTO;
import com.foru.blog.entity.FriendLink;
import com.foru.blog.vo.ConditionVO;
import com.foru.blog.vo.FriendLinkVO;
import com.foru.blog.vo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 友链服务
 *
 * @author 9527
 * @date 2021/07/29
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 查看友链列表
     *
     * @return 友链列表
     */
    List<FriendLinkDTO> listFriendLinks();

    /**
     * 查看后台友链列表
     *
     * @param condition 条件
     * @return 友链列表
     */
    PageResult<FriendLinkBackDTO> listFriendLinkDTO(ConditionVO condition);

    /**
     * 保存或更新友链
     *
     * @param friendLinkVO 友链
     */
    void saveOrUpdateFriendLink(FriendLinkVO friendLinkVO);

}
