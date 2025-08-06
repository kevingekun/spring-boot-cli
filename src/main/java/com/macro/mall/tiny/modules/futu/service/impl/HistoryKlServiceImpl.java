package com.macro.mall.tiny.modules.futu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.futu.mapper.HistoryKlMapper;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@Service
public class HistoryKlServiceImpl extends ServiceImpl<HistoryKlMapper, HistoryKl> implements HistoryKlService {

    @Override
    public List<HistoryKl> getHistoryKL(String code, String startDate, String endDate) {
        QueryWrapper<HistoryKl> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "code", "name", "change_rate", "close_price", "high_price", "low_price", "open_price", "data_time");
        queryWrapper.eq("code", code);
        if (StrUtil.isNotBlank(startDate)) {
            queryWrapper.ge("data_time", startDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            queryWrapper.le("data_time", endDate);
        }
        queryWrapper.orderByAsc("data_time");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void saveOrUpdateData(HistoryKl historyKl) {
        QueryWrapper<HistoryKl> queryWrapper = new QueryWrapper<>();
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
