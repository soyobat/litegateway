package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 路由配置DTO
 */
@Data
@ApiModel("路由配置参数")
public class RouteConfigDTO {

    @ApiModelProperty(value = "路由ID", required = true)
    @NotBlank(message = "路由ID不能为空")
    private String id;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    @ApiModelProperty(value = "URI", required = true)
    @NotBlank(message = "URI不能为空")
    private String uri;

    @ApiModelProperty(value = "谓词配置")
    private List<PredicateConfigDTO> predicates;

    @ApiModelProperty(value = "过滤器配置")
    private List<FilterConfigDTO> filters;

    @ApiModelProperty(value = "排序")
    private Integer order;

    @ApiModelProperty(value = "元数据")
    private Map<String, String> metadata;
}
