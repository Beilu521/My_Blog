package com.deerecho.My_Blog.service;

import java.util.Map;

public interface StatisticsService {
    Long getTotalViewCount();
    Long getTotalLikeCount();
    Long getTotalArticleCount();
    Long getTotalUserCount();
    void incrementTotalViewCount();
    void incrementTotalLikeCount();
    void incrementTotalArticleCount();
    void incrementTotalUserCount();
    Map<String, Object> getAllStatistics();
}
