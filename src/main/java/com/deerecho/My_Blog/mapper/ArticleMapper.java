package com.deerecho.My_Blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deerecho.My_Blog.entity.Article;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
      void incrementViewCount(@Param("articleId") Long articleId);
      void incrementLikeCount(@Param("articleId") Long articleId);
      void decrementLikeCount(@Param("articleId") Long articleId);
}