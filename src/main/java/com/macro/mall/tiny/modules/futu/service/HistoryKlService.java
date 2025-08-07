package com.macro.mall.tiny.modules.futu.service;

import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
public interface HistoryKlService extends IService<HistoryKl> {

    /**
     * 根据股票代码和日期范围获取历史K线数据
     *
     * @param code      股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 历史K线数据
     */
    List<HistoryKl> getHistoryKL(String code, String startDate, String endDate);

    /**
     * 保存或更新历史K线数据
     *
     * @param historyKl 历史K线数据
     */
    void saveOrUpdateData(HistoryKl historyKl);

    /**
     * 根据股票代码删除历史K线数据
     *
     * @param code 股票代码
     */
    void deleteByCode(String code);
}
