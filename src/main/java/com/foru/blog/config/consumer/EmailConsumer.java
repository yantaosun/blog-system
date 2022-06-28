package com.foru.blog.config.consumer;

import com.alibaba.fastjson.JSON;
import com.foru.blog.constant.CommonConst;
import com.foru.blog.constant.MQPrefixConst;
import com.foru.blog.dto.blog.EmailDTO;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;

/**
 * 通知邮箱
 *
 * @author 9527
 * @date 2021/06/13
 * @since 1.0.0
 **/
@Component
@RocketMQMessageListener(topic = MQPrefixConst.EMAIL_TOPIC,
        consumerGroup = MQPrefixConst.MQ_GROUP_EMAIL,
        consumeMode = ConsumeMode.CONCURRENTLY,
        selectorType = SelectorType.TAG,
        selectorExpression = MQPrefixConst.EMAIL_TOPIC_TAG_OF_USER)
public class EmailConsumer implements RocketMQListener<String> {

    private Logger logger = LoggerFactory.getLogger(EmailConsumer.class);
    /**
     * 邮箱号
     */
    @Value("${spring.mail.username}")
    private String email;

    private static final String TEMPLATE = "mail.html";

    private JavaMailSender javaMailSender;

    private TemplateEngine templateEngine;

    @Autowired
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    @Override
    public void onMessage(String payload) {
        try {
            EmailDTO emailDTO = JSON.parseObject(payload, EmailDTO.class);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
            messageHelper.setFrom(email);//邮件发信人
            messageHelper.setTo(emailDTO.getEmail());//邮件收件人
            messageHelper.setSubject(emailDTO.getSubject());//邮件主题
            Context context = new Context();
            context.setVariable(CommonConst.USERNAME,emailDTO.getUserName());
            context.setVariable(CommonConst.OPERATION,emailDTO.getOperation());
            context.setVariable(CommonConst.VERIFY_CODE,emailDTO.getContent().toCharArray());
            String emailContent = templateEngine.process(TEMPLATE,context);
            messageHelper.setText(emailContent,true);
            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            logger.error("发送邮件失败:{}",e);
        }
    }
}
