package com.foru.blog.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author 9527
 * @date 2022/04/30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLog {

    /**
     * 操作内容的EL表达式，仅支持方法参数
     */
    @AliasFor("content")
    String value() default "";

    /**
     * 操作内容，与 value 互为别名
     */
    @AliasFor("value")
    String content() default "";

    /**
     * 生效条件的EL表达式<p>
     * 它是通过返回值判断<p>
     * 示例（函数返回值为0）：<p>
     * EL表达式："#ret > 0"，结果 false
     */
    String condition() default "";

    /**
     * @return 操作类型
     */
    String optType() default "";

}
