package com.deerecho.My_Blog.controller;

import com.deerecho.My_Blog.common.Result;
import com.deerecho.My_Blog.entity.Comment;
import com.deerecho.My_Blog.entity.User;
import com.deerecho.My_Blog.interceptor.LoginInterceptor;
import com.deerecho.My_Blog.service.CommentService;
import com.deerecho.My_Blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping("/list/{articleId}")
    public Result<List<Comment>> list(@PathVariable Long articleId) {
        List<Comment> comments = commentService.getNestedComments(articleId);
        return Result.success(comments);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestParam Long articleId,
                             @RequestParam String content,
                             @RequestParam(required = false) Long parentId,
                             HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute(LoginInterceptor.USER_ROLE);
        if (role == null || role == User.ROLE_GUEST) {
            return Result.error(401, "请先登录后评论");
        }
        Long userId = (Long) request.getAttribute(LoginInterceptor.USER_ID);
        String nickname = (String) request.getAttribute(LoginInterceptor.USER_NICKNAME);

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setNickname(nickname);
        comment.setContent(content);
        comment.setParentId(parentId);
        if (parentId != null) {
            Comment parent = commentService.getById(parentId);
            if (parent != null) {
                comment.setParentNickname(parent.getNickname());
            }
        }
        commentService.save(comment);
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute(LoginInterceptor.USER_ROLE);
        if (role == null || role == User.ROLE_GUEST) {
            return Result.error(401, "请先登录");
        }
        Long userId = (Long) request.getAttribute(LoginInterceptor.USER_ID);
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        if (role != User.ROLE_ADMIN && !comment.getUserId().equals(userId)) {
            return Result.error("无权限删除此评论");
        }
        commentService.deleteWithReplies(id);
        return Result.success("删除成功", null);
    }
}
