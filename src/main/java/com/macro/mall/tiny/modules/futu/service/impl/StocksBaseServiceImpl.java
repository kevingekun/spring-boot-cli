package com.macro.mall.tiny.modules.futu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.mapper.StocksBaseMapper;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 股票基础信息 服务实现类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@Service
public class StocksBaseServiceImpl extends ServiceImpl<StocksBaseMapper, StocksBase> implements StocksBaseService {

    @Override
    public void saveStocksBase(StocksBase base) {
        QueryWrapper<StocksBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", base.getCode());
        queryWrapper.last("limit 1");
        StocksBase stocksBase = baseMapper.selectOne(queryWrapper);
        if (stocksBase == null) {
            baseMapper.insert(base);
        }
    }

    @Override
    public List<StocksBase> listAllOfOrder() {
        QueryWrapper<StocksBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("order_num");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public StocksBase getByCode(String code) {
        QueryWrapper<StocksBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer getMaxOrderNum() {
        QueryWrapper<StocksBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("order_num");
        queryWrapper.last("limit 1");
        StocksBase stocksBase = baseMapper.selectOne(queryWrapper);
        if (stocksBase != null) {
            return stocksBase.getOrderNum();
        }
        return 0;
    }
}
