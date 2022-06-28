package com.foru.blog.strategy;

import com.foru.blog.dto.blog.ArticleSearchDTO;

import java.util.List;

/**
 * 搜索策略
 *
 * @author 9527
 * @date 2021/07/27
 */
public interface SearchStrategy {

    /**
     * 搜索文章
     *
     * @param keywords 关键字
     * @return {@link List<ArticleSearchDTO>} 文章列表
     */
    List<ArticleSearchDTO> searchArticle(String keywords);

}
