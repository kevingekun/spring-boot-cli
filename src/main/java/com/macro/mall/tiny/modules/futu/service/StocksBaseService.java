package com.macro.mall.tiny.modules.futu.service;

import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 股票基础信息 服务类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
public interface StocksBaseService extends IService<StocksBase> {

    /**
     * 保存股票基础信息
     *
     * @param stocksBase 股票基础信息
     */
    void saveStocksBase(StocksBase stocksBase);

    /**
     * 获取所有股票基础信息，按排序号升序排列
     *
     * @return 所有股票基础信息
     */
    List<StocksBase> listAllOfOrder();

    /**
     * 根据股票代码获取股票基础信息
     *
     * @param code 股票代码
     * @return 股票基础信息
     */
    StocksBase getByCode(String code);

    /**
     * 获取最大排序号
     *
     * @return 最大排序号
     */
    Integer getMaxOrderNum();

    /**
     * 根据股票代码删除股票基础信息
     *
     * @param code 股票代码
     */
    void deleteByCode(String code);
}
