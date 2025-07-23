package com.macro.mall.tiny.modules.futu.service;

import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
public interface HistoryKlService extends IService<HistoryKl> {

    List<HistoryKl> getHistoryKL(String code);
}
