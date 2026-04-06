package com.deerecho.My_Blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deerecho.My_Blog.entity.Article;
import com.deerecho.My_Blog.mapper.ArticleMapper;
import com.deerecho.My_Blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
 
    // ====================== 常量定义（规范易维护） ======================
    private static final String LIKE_KEY_PREFIX = "blog:article:like:";
    private static final long LIKE_EXPIRE_DAYS = 7L;

    // ====================== 浏览量（原子操作，高并发安全） ======================
    @Override
    public void addViewCount(Long id) {
        // 直接用数据库原子+1，避免"先查再改"的并发问题
        articleMapper.incrementViewCount(id);
    }

    // ====================== 点赞（免登录+Redis防重+7天过期） ======================
    @Override
    public int likeArticle(Long articleId, String userIp) {
        String redisKey = LIKE_KEY_PREFIX + articleId;

        // 1. 检查是否已点赞（Redis Set判断）
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(redisKey, userIp))) {
            return 1; // 1 = 已点赞
        }

        // 2. 加入Redis，设置7天过期
        redisTemplate.opsForSet().add(redisKey, userIp);
        redisTemplate.expire(redisKey, LIKE_EXPIRE_DAYS, TimeUnit.DAYS);

        // 3. 数据库点赞数原子+1
        articleMapper.incrementLikeCount(articleId);
        return 0; // 0 = 点赞成功
    }

    // ====================== 取消点赞 ======================
    @Override
    public int unlikeArticle(Long articleId, String userIp) {
        String redisKey = LIKE_KEY_PREFIX + articleId;

        // 1. 检查是否已点赞
        if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(redisKey, userIp))) {
            return 1; // 1 = 未点赞，无法取消
        }

        // 2. 从Redis移除
        redisTemplate.opsForSet().remove(redisKey, userIp);

        // 3. 数据库点赞数原子-1（防止变成负数）
        articleMapper.decrementLikeCount(articleId);
        return 0; // 0 = 取消成功
    }

    // ====================== 获取点赞数（直接读Redis，性能高） ======================
    @Override
    public Long getLikeCount(Long articleId) {
        String redisKey = LIKE_KEY_PREFIX + articleId;
        Long size = redisTemplate.opsForSet().size(redisKey);
        return size != null ? size : 0L;
    }

    // ====================== 判断当前用户是否已点赞（前端按钮状态用） ======================
    @Override
    public boolean isLiked(Long articleId, String userIp) {
        String redisKey = LIKE_KEY_PREFIX + articleId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(redisKey, userIp));
    }
}