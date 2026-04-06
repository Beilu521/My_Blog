package com.deerecho.My_Blog.controller;

import com.deerecho.My_Blog.common.Result;
import com.deerecho.My_Blog.entity.Article;
import com.deerecho.My_Blog.entity.User;
import com.deerecho.My_Blog.interceptor.LoginInterceptor;
import com.deerecho.My_Blog.service.ArticleService;
import com.deerecho.My_Blog.service.StatisticsService;
import com.deerecho.My_Blog.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/list")
    public Result<List<Article>> list() {
        List<Article> articles = articleService.list();
        return Result.success(articles);
    }

    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Long id, HttpServletRequest request) {
        Article article = articleService.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        articleService.addViewCount(id);
        statisticsService.incrementTotalViewCount();
        request.setAttribute(LoginInterceptor.USER_ROLE, User.ROLE_GUEST);
        return Result.success(article);
    }

    @GetMapping("/{id}/like")
    public Result<Map<String, Object>> like(@PathVariable Long id, HttpServletRequest request) {
        String userIp = IpUtil.getIp(request);
        int status = articleService.likeArticle(id, userIp);
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        if (status == 0) {
            statisticsService.incrementTotalLikeCount();
        }
        result.put("likeCount", articleService.getLikeCount(id));
        return Result.success(result);
    }

    @GetMapping("/{id}/unlike")
    public Result<Map<String, Object>> unlike(@PathVariable Long id, HttpServletRequest request) {
        String userIp = IpUtil.getIp(request);
        int status = articleService.unlikeArticle(id, userIp);
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("likeCount", articleService.getLikeCount(id));
        return Result.success(result);
    }

    @GetMapping("/{id}/isLiked")
    public Result<Boolean> isLiked(@PathVariable Long id, HttpServletRequest request) {
        String userIp = IpUtil.getIp(request);
        boolean liked = articleService.isLiked(id, userIp);
        return Result.success(liked);
    }

    @GetMapping("/{id}/download")
    public Result<Map<String, Object>> download(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute(LoginInterceptor.USER_ROLE);
        if (role == null || role == User.ROLE_GUEST) {
            return Result.error(401, "请先登录后下载");
        }
        Article article = articleService.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("title", article.getTitle());
        result.put("content", article.getContent());
        result.put("category", article.getCategory());
        return Result.success(result);
    }
}
