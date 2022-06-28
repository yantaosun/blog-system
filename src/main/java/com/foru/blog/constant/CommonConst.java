package com.foru.blog.constant;


/**
 * 公共常量
 *
 * @author 9527
 * @date 2021/07/27
 */
public class CommonConst {

    /**
     * 否
     */
    public static final int FALSE = 0;

    /**
     * 是
     */
    public static final int TRUE = 1;

    /**
     * 高亮标签
     */
    public static final String PRE_TAG = "<span style='color:#f47466'>";

    /**
     * 高亮标签
     */
    public static final String POST_TAG = "</span>";

    /**
     * 当前页码
     */
    public static final String CURRENT = "current";

    /**
     * 页码条数
     */
    public static final String SIZE = "size";

    /**
     * 博主id
     */
    public static final Integer BLOGGER_ID = 1;

    /**
     * 默认条数
     */
    public static final String DEFAULT_SIZE = "10";

    /**
     * 默认用户昵称
     */
    public static final String DEFAULT_NICKNAME = "用户";

    /**
     * 浏览文章集合
     */
    public static String ARTICLE_SET = "articleSet";

    /**
     * 前端组件名
     */
    public static String COMPONENT = "Layout";

    /**
     * 省
     */
    public static final String PROVINCE = "省";

    /**
     * 市
     */
    public static final String CITY = "市";

    /**
     * 未知的
     */
    public static final String UNKNOWN = "未知";

    /**
     * JSON 格式
     */
    public static final String APPLICATION_JSON = "application/json;charset=utf-8";

    /**
     * 默认的配置id
     */
    public static final Integer DEFAULT_CONFIG_ID = 1;

    /**
     * 成功
     */
    public static final String SUCCESS = "成功";

    /**
     * 失败
     */
    public static final String FAIL = "失败";

    /**
     * ID
     */
    public static final String ID_FIELD = "ID";

    /**
     * 密码
     */
    public static final String PASSWORD_FIELD = "密码";


    /**
     * 发送邮件格式化字段
     */
    public static final String USERNAME = "username";

    public static final String OPERATION = "operation";

    public static final String VERIFY_CODE = "verifyCode";

    public static final String SUBJECT = "验证码通知";


    public enum EmailOperation{

        BIND_EMAIL(101,"绑定邮箱") ,

        FORGET_PASS(102,"找回密码"),

        REGISTER_USER(103,"注册用户");

        private Integer type;
        private String content;

        public Integer getType() {
            return type;
        }

        public String getContent() {
            return content;
        }

        EmailOperation(Integer type, String content) {
            this.type = type;
            this.content = content;
        }
        
        public static String ofType(Integer type){
            for (EmailOperation operation:EmailOperation.values()) {
                if(operation.getType() == type){
                    return operation.getContent();
                }
            }
            return null;
        }

    }

}
