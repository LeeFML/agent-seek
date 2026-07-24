package com.agentseek.plugin.loader;

import com.agentseek.plugin.AgentPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件加载器与管理器
 */
@Slf4j
@Component
public class PluginLoader {

    private final Map<String, AgentPlugin> plugins = new ConcurrentHashMap<>();

    /**
     * 注册插件
     */
    public void register(AgentPlugin plugin) {
        plugins.put(plugin.getName(), plugin);
        log.info("Plugin registered: {} (type={})", plugin.getName(), plugin.getType());
    }

    /**
     * 获取插件
     */
    public AgentPlugin getPlugin(String name) {
        return plugins.get(name);
    }

    /**
     * 列出所有插件
     */
    public List<AgentPlugin> listPlugins() {
        return List.copyOf(plugins.values());
    }
}
