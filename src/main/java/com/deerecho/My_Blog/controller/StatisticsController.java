package com.deerecho.My_Blog.controller;

import com.deerecho.My_Blog.common.Result;
import com.deerecho.My_Blog.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(statisticsService.getAllStatistics());
    }
}
