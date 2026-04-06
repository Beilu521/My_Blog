package com.deerecho.My_Blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.deerecho.My_Blog.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> getNestedComments(Long articleId);
    void deleteWithReplies(Long commentId);
}
