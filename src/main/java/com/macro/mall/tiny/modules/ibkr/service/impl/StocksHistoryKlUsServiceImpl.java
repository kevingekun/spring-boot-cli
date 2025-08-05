package com.macro.mall.tiny.modules.ibkr.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.ibkr.model.StocksHistoryKlUs;
import com.macro.mall.tiny.modules.ibkr.mapper.StocksHistoryKlUsMapper;
import com.macro.mall.tiny.modules.ibkr.service.StocksHistoryKlUsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 股票历史 K 线 服务实现类
 * </p>
 *
 * @author macro
 * @since 2025-08-04
 */
@Service
public class StocksHistoryKlUsServiceImpl extends ServiceImpl<StocksHistoryKlUsMapper, StocksHistoryKlUs> implements StocksHistoryKlUsService {

    @Override
    public List<StocksHistoryKlUs> getHistoryKL(String code, String startDate, String endDate) {
        QueryWrapper<StocksHistoryKlUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "code", "name", "change_rate", "close_price", "high_price", "low_price", "open_price", "data_time");
        queryWrapper.eq("code", code);
        if (StrUtil.isNotBlank(startDate)) {
            queryWrapper.ge("data_time", startDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            queryWrapper.le("data_time", endDate);
        }
        queryWrapper.orderByAsc("id");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void saveOrUpdateData(StocksHistoryKlUs historyKl) {
        QueryWrapper<StocksHistoryKlUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", historyKl.getCode());
        queryWrapper.eq("data_time", historyKl.getDataTime());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            baseMapper.update(historyKl, queryWrapper);
        } else {
            historyKl.setCreateDate(new Date());
            baseMapper.insert(historyKl);
        }
    }
}
