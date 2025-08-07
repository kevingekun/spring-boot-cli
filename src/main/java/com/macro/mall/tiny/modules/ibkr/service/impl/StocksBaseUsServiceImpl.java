package com.macro.mall.tiny.modules.ibkr.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.ibkr.model.StocksBaseUs;
import com.macro.mall.tiny.modules.ibkr.mapper.StocksBaseUsMapper;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 股票基础信息 服务实现类
 * </p>
 *
 * @author macro
 * @since 2025-08-04
 */
@Service
public class StocksBaseUsServiceImpl extends ServiceImpl<StocksBaseUsMapper, StocksBaseUs> implements StocksBaseUsService {

    @Override
    public void saveStocksBaseUs(String code, String name) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        QueryWrapper<StocksBaseUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        queryWrapper.last("limit 1");
        StocksBaseUs stocksBaseUs = baseMapper.selectOne(queryWrapper);
        if (stocksBaseUs == null) {
            stocksBaseUs = new StocksBaseUs();
            stocksBaseUs.setCode(code);
            stocksBaseUs.setName(name);
            stocksBaseUs.setOrderNum(getMaxOrderNum() + 1);
            stocksBaseUs.setCreateDate(new Date());
            baseMapper.insert(stocksBaseUs);
        }
    }

    @Override
    public Integer getMaxOrderNum() {
        QueryWrapper<StocksBaseUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("order_num");
        queryWrapper.last("limit 1");
        StocksBaseUs stocksBaseUs = baseMapper.selectOne(queryWrapper);
        if (stocksBaseUs != null) {
            return stocksBaseUs.getOrderNum();
        }
        return 0;
    }

    @Override
    public StocksBaseUs getByCode(String code) {
        QueryWrapper<StocksBaseUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<StocksBaseUs> listAllOfOrder() {
        QueryWrapper<StocksBaseUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("order_num");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteByCode(String code) {
        QueryWrapper<StocksBaseUs> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code.toUpperCase());
        baseMapper.delete(queryWrapper);
    }
}
