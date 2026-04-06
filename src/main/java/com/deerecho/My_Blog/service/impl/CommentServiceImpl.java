package com.deerecho.My_Blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deerecho.My_Blog.entity.Comment;
import com.deerecho.My_Blog.mapper.CommentMapper;
import com.deerecho.My_Blog.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    @Transactional
    public void deleteWithReplies(Long commentId) {
        List<Long> idsToDelete = new ArrayList<>();
        collectAllReplyIds(commentId, idsToDelete);
        if (!idsToDelete.isEmpty()) {
            removeBatchByIds(idsToDelete);
        }
    }

    private void collectAllReplyIds(Long parentId, List<Long> ids) {
        ids.add(parentId);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<Comment> children = list(wrapper);
        for (Comment child : children) {
            collectAllReplyIds(child.getId(), ids);
        }
    }

    @Override
    public List<Comment> getNestedComments(Long articleId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        wrapper.orderByAsc("create_time");
        List<Comment> allComments = list(wrapper);
        return buildNestedList(allComments, null);
    }

    private List<Comment> buildNestedList(List<Comment> allComments, Long parentId) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : allComments) {
            if ((parentId == null && comment.getParentId() == null) ||
                (parentId != null && parentId.equals(comment.getParentId()))) {
                List<Comment> children = buildNestedList(allComments, comment.getId());
                comment.setChildren(children);
                result.add(comment);
            }
        }
        return result;
    }
}
