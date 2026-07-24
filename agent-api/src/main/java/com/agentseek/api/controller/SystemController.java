package com.agentseek.api.controller;

import com.agentseek.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查与系统状态
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "UP");
        map.put("time", LocalDateTime.now().toString());
        map.put("version", "0.1.0-SNAPSHOT");
        return Result.ok(map);
    }
}
