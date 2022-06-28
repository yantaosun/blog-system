package com.foru.blog.constant;

/**
 * mqprefix常量
 * mq常量
 *
 * @author 9527
 * @date 2021/07/28
 */
public class MQPrefixConst {

    /**
     * maxwell队列
     */
    public static final String MAXWELL_TOPIC = "maxwell_topic";


    /**
     * 邮件队列
     */
    public static final String EMAIL_TOPIC = "email_topic";
    public static final String EMAIL_TOPIC_TAG_OF_USER = "email_topic_of_user";
    public static final String EMAIL_TOPIC_TAG_OF_TALKS = "email_topic_of_talks";


    /**
     * 日志队列
     */
    public static final String LOG_TOPIC = "log_topic";


    /**
     * 生产消费组-email
     */
    public static final String MQ_GROUP_EMAIL = "blog-group-email";

    /**
     * 生产消费组-log
     */
    public static final String MQ_GROUP_LOG = "blog-group-log";

    /**
     * 生产消费组-maxwell
     */
    public static final String MQ_GROUP_MAXWELL = "blog-group-max";
}
