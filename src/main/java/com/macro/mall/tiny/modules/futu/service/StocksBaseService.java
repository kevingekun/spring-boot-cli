package com.macro.mall.tiny.modules.futu.service;

import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 股票基础信息 服务类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
public interface StocksBaseService extends IService<StocksBase> {

    void saveStocksBase(StocksBase stocksBase);
}
