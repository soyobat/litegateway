package com.grace.gateway.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grace.gateway.admin.entity.GatewayConfigEntity;
import com.grace.gateway.admin.query.GatewayConfigQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网关配置Mapper接口
 */
@Mapper
public interface GatewayConfigMapper extends BaseMapper<GatewayConfigEntity> {

    /**
     * 分页查询网关配置
     *
     * @param page 分页参数
     * @param query 查询条件
     * @return 网关配置列表
     */
    IPage<GatewayConfigEntity> selectPageByQuery(Page<GatewayConfigEntity> page, @Param("query") GatewayConfigQuery query);

    /**
     * 根据网关名称查询配置
     *
     * @param gatewayName 网关名称
     * @return 网关配置
     */
    GatewayConfigEntity selectByGatewayName(@Param("gatewayName") String gatewayName);

    /**
     * 查询所有启用的网关配置
     *
     * @return 网关配置列表
     */
    List<GatewayConfigEntity> selectAllEnabled();
}
