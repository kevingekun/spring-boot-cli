package com.macro.mall.tiny.modules.ibkr.schedule;

import com.macro.mall.tiny.modules.ibkr.component.HistoricalDataComponent;
import com.macro.mall.tiny.modules.ibkr.model.StocksBaseUs;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时更新数据
 */
@Slf4j
@Component
@AllArgsConstructor
public class UpdateDataUsSchedule {

    private final StocksBaseUsService stocksBaseUsService;
    private final HistoricalDataComponent historicalDataComponent;

    //每周二到周六凌晨 4 点更新一次
    @Scheduled(cron = "0 0 4 ? * TUE-SAT")
    public void updateData() {
        log.info("定时更新数据 开始 凌晨");
        updateDataHandler();
        log.info("定时更新数据 结束 凌晨");
    }

    //每周一到周五的 21:30-24:00 每10分钟更新一次
    @Scheduled(cron = "0 0/10 21-23 ? * MON-FRI")
    public void updateData2() {
        log.info("定时更新数据 开始 晚上");
        updateDataHandler();
        log.info("定时更新数据 结束 晚上");
    }

    private void updateDataHandler() {
        List<StocksBaseUs> stocksBaseList = stocksBaseUsService.list();
        log.info("定时更新数据 股票数量:{}", stocksBaseList.size());
        for (StocksBaseUs base : stocksBaseList) {
            //更新数据
            historicalDataComponent.getLastKL(base.getCode());
            log.info("定时更新数据 股票:{}", base.getCode());
        }
    }


}
