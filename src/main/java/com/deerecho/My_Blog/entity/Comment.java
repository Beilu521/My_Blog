package com.deerecho.My_Blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_comment")
public class Comment extends BaseEntity {
    private Long articleId;
    private Long userId;
    private String nickname;
    private String content;
    private Long parentId;
    private String parentNickname;
    @TableField(exist = false)
    private List<Comment> children;
}
