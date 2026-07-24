package com.agentseek.core.mapper;

import com.agentseek.core.entity.AgentConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Agent 配置 Mapper
 */
@Mapper
public interface AgentConfigMapper extends BaseMapper<AgentConfig> {

    @Select("SELECT * FROM agent_config WHERE is_enabled = 1 AND deleted = 0")
    List<AgentConfig> selectEnabled();

    @Select("SELECT * FROM agent_config WHERE agent_id = #{agentId} AND deleted = 0 LIMIT 1")
    AgentConfig selectByAgentId(@Param("agentId") String agentId);
}
