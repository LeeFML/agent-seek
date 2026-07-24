package com.agentseek.core.mapper;

import com.agentseek.core.entity.PluginConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 插件配置 Mapper
 */
@Mapper
public interface PluginConfigMapper extends BaseMapper<PluginConfig> {

    @Select("SELECT * FROM plugin_config WHERE is_enabled = 1 AND deleted = 0 ORDER BY sort_order")
    List<PluginConfig> selectEnabled();
}
