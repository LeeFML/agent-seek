package com.agentseek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Agent Seek 启动入口
 */
@SpringBootApplication
@EnableScheduling
public class AgentSeekApplication {

    public static void main(String[] args) {
        //入口
        SpringApplication.run(AgentSeekApplication.class, args);
    }
}
