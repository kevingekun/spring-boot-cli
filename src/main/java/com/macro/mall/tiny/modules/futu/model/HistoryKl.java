package com.macro.mall.tiny.modules.futu.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("history_kl")
@ApiModel(value = "HistoryKl对象", description = "")
public class HistoryKl implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String name;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private BigDecimal changeRate;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date dataTime;

    private Date createDate;

    private Date updateDate;


}
