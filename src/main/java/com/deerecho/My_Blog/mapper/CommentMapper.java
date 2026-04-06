package com.deerecho.My_Blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deerecho.My_Blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}