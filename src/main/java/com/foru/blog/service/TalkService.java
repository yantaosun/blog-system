package com.foru.blog.service;

import com.foru.blog.dto.blog.TalkBackDTO;
import com.foru.blog.dto.blog.TalkDTO;
import com.foru.blog.entity.Talk;
import com.foru.blog.vo.ConditionVO;
import com.foru.blog.vo.PageResult;
import com.foru.blog.vo.TalkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 说说服务
 *
 * @author 9527
 * @date 2022/01/23
 */
public interface TalkService extends IService<Talk> {

    /**
     * 获取首页说说列表
     *
     * @return {@link List<String>} 说说列表
     */
    List<String> listHomeTalks();

    /**
     * 获取说说列表
     *
     * @return {@link PageResult < TalkDTO >} 说说列表
     */
    PageResult<TalkDTO> listTalks();

    /**
     * 根据id查看说说
     *
     * @param talkId 说说id
     * @return {@link TalkDTO} 说说信息
     */
    TalkDTO getTalkById(Integer talkId);

    /**
     * 点赞说说
     *
     * @param talkId 说说id
     */
    void saveTalkLike(Integer talkId);

    /**
     * 保存或修改说说
     *
     * @param talkVO 说说信息
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 删除说说
     *
     * @param talkIdList 说说id列表
     */
    void deleteTalks(List<Integer> talkIdList);

    /**
     * 查看后台说说
     *
     * @param conditionVO 条件
     * @return {@link PageResult< TalkBackDTO >}
     */
    PageResult<TalkBackDTO> listBackTalks(ConditionVO conditionVO);

    /**
     * 根据id查看后台说说
     *
     * @param talkId 说说id
     * @return {@link TalkBackDTO} 说说信息
     */
    TalkBackDTO getBackTalkById(Integer talkId);

}
