package com.deerecho.My_Blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)// 调用父类的equals方法
@TableName("blog_article")
public class Article extends BaseEntity {
    private String title;// 文章标题
    private String content;// 文章内容
    private String category;// 文章分类
    private String coverUrl;// 文章封面
    private Integer viewCount;// 文章浏览量
    private Integer likeCount;// 文章点赞量
}