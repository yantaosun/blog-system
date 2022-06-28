package com.foru.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件路径枚举
 *
 * @author 9527
 * @date 2021/08/04
 */
@Getter
@AllArgsConstructor
public enum FilePathEnum {
    /**
     * 头像路径
     */
    AVATAR("upload/avatar/", "头像路径"),
    /**
     * 文章图片路径
     */
    ARTICLE("upload/articles/", "文章图片路径"),
    /**
     * 音频路径
     */
    VOICE("upload/voice/", "音频路径"),
    /**
     * 照片路径
     */
    PHOTO("upload/photos/","相册路径"),
    /**
     * 配置图片路径
     */
    CONFIG("upload/config/","配置图片路径"),
    /**
     * 说说图片路径
     */
    TALK("upload/talks/","配置图片路径");

    /**
     * 路径
     */
    private final String path;

    /**
     * 描述
     */
    private final String desc;

}
