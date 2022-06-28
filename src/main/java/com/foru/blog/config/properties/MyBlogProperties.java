package com.foru.blog.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * 博客配置
 *
 * @author Myles Yang
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "my-blog")
public class MyBlogProperties {
	/**
	 * swagger doc
	 */
	private boolean docEnable = false;

	/**
	 * 后台管理入口，不需要添加'/'
	 */
	private String adminPath = "admin";

	/**
	 * 自定义管理端WEB静态文件所在目录，默认为“classpath:/admin/”
	 */
	private String adminWebPath = null;

	/**
	 * 自定义展示前端WEB静态文件所在目录，默认为“classpath:/app/”
	 */
	private String appWebPath = null;

	/**
	 * 允许连续登录失败的时间（单位秒）
	 */
	private int allowLoginFailureSeconds = 3600;

	/**
	 * 允许登录失败的次数
	 */
	private int allowLoginFailureCount = 3;


	/**
	 * 登录记住我token时间(单位秒，1周 = 604800秒)
	 */
	private int rememberMeTokenValiditySeconds = 604800;

	/**
	 * 上传文件保存路径
	 */
	private String fileSavePath = Paths.get(System.getProperty("user.home"), "MyBlog", "files").toString();

	/**
	 * 登录记住我key
	 */
	private String rememberMeTokenKey = "user:remember:me:token:";

}
