package com.deerecho.My_Blog.service.impl;

import com.deerecho.My_Blog.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_TOTAL_VIEW = "blog:statistics:total_view";
    private static final String KEY_TOTAL_LIKE = "blog:statistics:total_like";
    private static final String KEY_TOTAL_ARTICLE = "blog:statistics:total_article";
    private static final String KEY_TOTAL_USER = "blog:statistics:total_user";

    @Override
    public Long getTotalViewCount() {
        String value = redisTemplate.opsForValue().get(KEY_TOTAL_VIEW);
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public Long getTotalLikeCount() {
        String value = redisTemplate.opsForValue().get(KEY_TOTAL_LIKE);
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public Long getTotalArticleCount() {
        String value = redisTemplate.opsForValue().get(KEY_TOTAL_ARTICLE);
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public Long getTotalUserCount() {
        String value = redisTemplate.opsForValue().get(KEY_TOTAL_USER);
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public void incrementTotalViewCount() {
        redisTemplate.opsForValue().increment(KEY_TOTAL_VIEW);
    }

    @Override
    public void incrementTotalLikeCount() {
        redisTemplate.opsForValue().increment(KEY_TOTAL_LIKE);
    }

    @Override
    public void incrementTotalArticleCount() {
        redisTemplate.opsForValue().increment(KEY_TOTAL_ARTICLE);
    }

    @Override
    public void incrementTotalUserCount() {
        redisTemplate.opsForValue().increment(KEY_TOTAL_USER);
    }

    @Override
    public Map<String, Object> getAllStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalViewCount", getTotalViewCount());
        stats.put("totalLikeCount", getTotalLikeCount());
        stats.put("totalArticleCount", getTotalArticleCount());
        stats.put("totalUserCount", getTotalUserCount());
        return stats;
    }
}
