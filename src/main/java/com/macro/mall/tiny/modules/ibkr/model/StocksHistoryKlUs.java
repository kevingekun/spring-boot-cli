package com.macro.mall.tiny.modules.ibkr.model;

import java.math.BigDecimal;
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
 * 股票历史 K 线
 * </p>
 *
 * @author macro
 * @since 2025-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stocks_history_kl_us")
@ApiModel(value="StocksHistoryKlUs对象", description="股票历史 K 线")
public class StocksHistoryKlUs implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private BigDecimal openPrice;

    private BigDecimal closePrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    private BigDecimal changeRate;

    private Date dataTime;

    private Date createDate;

    private Date updateDate;


}
