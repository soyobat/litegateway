package com.grace.gateway.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grace.gateway.admin.entity.GatewayInstanceEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网关实例Mapper接口
 */
@Mapper
public interface GatewayInstanceMapper extends BaseMapper<GatewayInstanceEntity> {
}
