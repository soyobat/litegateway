package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 路由模板VO
 */
@Data
@ApiModel("路由模板")
public class RouteTemplateVO {

    @ApiModelProperty("路由ID")
    private String id;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("URI")
    private String uri;

    @ApiModelProperty("谓词配置")
    private List<PredicateConfigVO> predicates;

    @ApiModelProperty("过滤器配置")
    private List<FilterConfigVO> filters;

    @ApiModelProperty("排序")
    private Integer order;

    @ApiModelProperty("元数据")
    private Map<String, String> metadata;
}
