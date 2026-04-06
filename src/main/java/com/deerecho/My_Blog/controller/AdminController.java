package com.deerecho.My_Blog.controller;

import com.deerecho.My_Blog.common.Result;
import com.deerecho.My_Blog.entity.Article;
import com.deerecho.My_Blog.interceptor.LoginInterceptor;
import com.deerecho.My_Blog.service.ArticleService;
import com.deerecho.My_Blog.service.StatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/article/add")
    public Result<String> addArticle(@RequestBody Article article, HttpServletRequest request) {
        articleService.save(article);
        statisticsService.incrementTotalArticleCount();
        return Result.success("文章创建成功", null);
    }

    @PutMapping("/article/update")
    public Result<String> updateArticle(@RequestBody Article article, HttpServletRequest request) {
        articleService.updateById(article);
        return Result.success("文章更新成功", null);
    }

    @DeleteMapping("/article/{id}")
    public Result<String> deleteArticle(@PathVariable Long id, HttpServletRequest request) {
        articleService.removeById(id);
        return Result.success("文章删除成功", null);
    }

    @GetMapping("/article/list")
    public Result<List<Article>> listArticles(HttpServletRequest request) {
        List<Article> articles = articleService.list();
        return Result.success(articles);
    }

    @GetMapping("/article/{id}")
    public Result<Article> getArticle(@PathVariable Long id, HttpServletRequest request) {
        Article article = articleService.getById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }
}
