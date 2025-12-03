package com.grace.gateway.admin.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 分页响应结果
 */
@Data
@ApiModel("分页响应结果")
public class PageResult<T> {

    @ApiModelProperty("数据列表")
    private List<T> records;

    @ApiModelProperty("总记录数")
    private Long total;

    @ApiModelProperty("当前页码")
    private Long current;

    @ApiModelProperty("每页大小")
    private Long size;

    @ApiModelProperty("总页数")
    private Long pages;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }
}
