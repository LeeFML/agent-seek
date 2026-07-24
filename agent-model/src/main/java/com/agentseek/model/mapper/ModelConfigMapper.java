package com.agentseek.model.mapper;

import com.agentseek.model.config.ModelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 模型配置 Mapper
 */
@Mapper
public interface ModelConfigMapper extends BaseMapper<ModelConfig> {

    /**
     * 查询默认启用的模型配置
     */
    @Select("SELECT * FROM model_config WHERE is_default = 1 AND is_enabled = 1 AND deleted = 0 LIMIT 1")
    ModelConfig selectDefault();

    /**
     * 根据提供商查询
     */
    @Select("SELECT * FROM model_config WHERE provider = #{provider} AND is_enabled = 1 AND deleted = 0 LIMIT 1")
    ModelConfig selectByProvider(@Param("provider") String provider);
}
