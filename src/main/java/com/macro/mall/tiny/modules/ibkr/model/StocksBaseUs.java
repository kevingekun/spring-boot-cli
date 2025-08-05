package com.macro.mall.tiny.modules.ibkr.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2025-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stocks_base_us")
@ApiModel(value="StocksBaseUs对象", description="股票基础信息")
public class StocksBaseUs implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String code;

    private String content;

    private Integer orderNum;

    private Date createDate;


}
