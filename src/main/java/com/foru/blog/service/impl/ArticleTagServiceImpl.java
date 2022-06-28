package com.foru.blog.service.impl;

import com.foru.blog.entity.ArticleTag;
import com.foru.blog.dao.ArticleTagDao;
import com.foru.blog.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章标签服务
 *
 * @author 9527
 * @date 2021/08/10
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTag> implements ArticleTagService {

}
