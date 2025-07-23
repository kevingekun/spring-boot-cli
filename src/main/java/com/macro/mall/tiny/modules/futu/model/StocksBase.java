package com.macro.mall.tiny.modules.futu.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 股票基础信息
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stocks_base")
@ApiModel(value="StocksBase对象", description="股票基础信息")
public class StocksBase implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    private String name;

    private String code;

    private String content;

    private Integer order;

    private Date createDate;


}
