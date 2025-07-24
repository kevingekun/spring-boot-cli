package com.macro.mall.tiny.modules.futu.schedule;

import com.macro.mall.tiny.modules.futu.component.HistoryKLComponent;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时更新数据
 */
@Slf4j
@Component
@AllArgsConstructor
public class UpdateDataSchedule {

    private final StocksBaseService stocksBaseService;
    private final ApplicationContext applicationContext;

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateData() {
        log.info("定时更新数据 开始");
        List<StocksBase> stocksBaseList = stocksBaseService.list();
        log.info("定时更新数据 股票数量:{}", stocksBaseList.size());
        for (StocksBase base : stocksBaseList) {
            //更新数据
            HistoryKLComponent historyKLComponent = applicationContext.getBean(HistoryKLComponent.class);
            historyKLComponent.getLastKL(base.getCode());
            log.info("定时更新数据 股票:{}", base.getCode());
        }
        log.info("定时更新数据 结束");
    }
}
