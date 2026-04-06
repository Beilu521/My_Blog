package com.deerecho.My_Blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.deerecho.My_Blog.entity.Article;

public interface ArticleService extends IService<Article> {
    void addViewCount(Long id);
    int likeArticle(Long articleId, String userIp);
    int unlikeArticle(Long articleId, String userIp);
    Long getLikeCount(Long articleId);
    boolean isLiked(Long articleId, String userIp);
}