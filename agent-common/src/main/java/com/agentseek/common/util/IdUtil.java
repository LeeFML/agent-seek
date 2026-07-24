package com.agentseek.common.util;

import java.util.UUID;

/**
 * ID 生成工具
 */
public class IdUtil {

    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String generateAgentId() {
        return "AGENT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}
