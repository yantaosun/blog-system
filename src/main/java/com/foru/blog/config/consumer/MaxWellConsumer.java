package com.foru.blog.config.consumer;

import com.alibaba.fastjson.JSON;
import com.foru.blog.constant.MQPrefixConst;
import com.foru.blog.dao.ElasticsearchDao;
import com.foru.blog.dto.blog.ArticleSearchDTO;
import com.foru.blog.dto.blog.MaxwellDataDTO;
import com.foru.blog.entity.Article;
import com.foru.blog.util.BeanCopyUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * maxwell监听数据
 *
 * @author 9527
 * @date 2021/08/02
 */
@Configuration
@ConditionalOnBean(ElasticsearchRestTemplate.class)
@RocketMQMessageListener(topic = MQPrefixConst.MAXWELL_TOPIC, consumerGroup = MQPrefixConst.MQ_GROUP_MAXWELL)
public class MaxWellConsumer implements RocketMQListener<String> {

    private ElasticsearchDao elasticsearchDao;

    @Autowired
    public void setElasticsearchDao(ElasticsearchDao elasticsearchDao) {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Override
    public void onMessage(String messge) {
        // 获取监听信息
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(messge, MaxwellDataDTO.class);
        // 获取文章数据
        Article article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), Article.class);
        // 判断操作类型
        switch (maxwellDataDTO.getType()) {
            case "insert":
            case "update":
                // 更新es文章
                elasticsearchDao.save(BeanCopyUtils.copyObject(article, ArticleSearchDTO.class));
                break;
            case "delete":
                // 删除文章
                elasticsearchDao.deleteById(article.getId());
                break;
            default:
                break;
        }
    }
}