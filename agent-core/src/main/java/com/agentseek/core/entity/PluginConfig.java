package com.agentseek.core.entity;

import com.agentseek.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件配置表
 * 数据库持久化的插件注册信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("plugin_config")
public class PluginConfig extends BaseEntity {

    /** 插件名称 */
    private String pluginName;

    /** 插件类型 */
    private String pluginType;

    /** 全限定类名 */
    private String className;

    /** 插件描述 */
    private String description;

    /** 版本号 */
    private String version;

    /** 是否启用 */
    private Boolean isEnabled;

    /** 配置参数 JSON */
    private String configJson;

    /** 排序 */
    private Integer sortOrder;
}
