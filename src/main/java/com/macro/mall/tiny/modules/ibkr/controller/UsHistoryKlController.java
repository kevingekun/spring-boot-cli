package com.macro.mall.tiny.modules.ibkr.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.futu.component.HistoryKLComponent;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.response.HistoryKLResp;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import com.macro.mall.tiny.modules.ibkr.component.HistoricalDataComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@Slf4j
@RestController
@RequestMapping("/us/historyKl")
@AllArgsConstructor
public class UsHistoryKlController {

    private final HistoryKlService historyKlService;
    private final ApplicationContext applicationContext;
    private final StocksBaseService stocksBaseService;

    @GetMapping("/test")
    public CommonResult<Object> tst(){
        HistoricalDataComponent historicalDataComponent = applicationContext.getBean(HistoricalDataComponent.class);
        historicalDataComponent.requestStockInfo("AAPL");
//        historicalDataComponent.requestHistoricalData("AAPL");
        return CommonResult.success(historicalDataComponent);
    }

    /**
     * 增加订阅一个股票的历史数据
     *
     * @param code 股票代码
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HistoryKl>> add(@RequestParam("code") String code) {
        log.info("subscribe code: {}", code);
        StocksBase stocksBase = stocksBaseService.getByCode(code);
        if (stocksBase != null) {
            log.info("subscribe already stocksBase: {}", stocksBase);
            return CommonResult.failed("股票已经订阅过了");
        }
        HistoryKLComponent historyKLComponent = applicationContext.getBean(HistoryKLComponent.class);
        historyKLComponent.getHistoryKL(code, null);
        return CommonResult.success(new ArrayList<>());
    }

    /**
     * 根据 code 查询全部历史数据
     *
     * @param code      股票代码
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HistoryKLResp> list(@RequestParam("code") String code,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) {
        log.info("code: {}, startDate: {}, endDate: {}", code, startDate, endDate);
        HistoryKLResp resp = new HistoryKLResp();
        resp.setCode(code);
        List<HistoryKl> historyKlList = historyKlService.getHistoryKL(code, startDate, endDate);
        if (CollectionUtil.isNotEmpty(historyKlList)) {
            String name = historyKlList.get(0).getName();
            resp.setName(name);
            resp.setHistoryKlList(historyKlList);
        }
        return CommonResult.success(resp);
    }
}

