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
import com.macro.mall.tiny.modules.ibkr.component.HistoricalDataWebApiComponent;
import com.macro.mall.tiny.modules.ibkr.model.StocksBaseUs;
import com.macro.mall.tiny.modules.ibkr.model.StocksHistoryKlUs;
import com.macro.mall.tiny.modules.ibkr.response.HistoryKLUSResp;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import com.macro.mall.tiny.modules.ibkr.service.StocksHistoryKlUsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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

    private final StocksHistoryKlUsService stocksHistoryKlUsService;
    private final StocksBaseUsService stocksBaseUsService;
    private final HistoricalDataComponent historicalDataComponent;
    private final HistoricalDataWebApiComponent historicalDataWebApiComponent;

/*    @GetMapping("/test")
    public CommonResult<Object> tst(){
//        historicalDataComponent.requestStockInfo("AAPL");
//        historicalDataComponent.requestHistoricalData("AAPL");
        historicalDataComponent.getLastKL("AAPL");
        return CommonResult.success(historicalDataComponent);
    }*/

    /**
     * 增加订阅一个股票的历史数据
     *
     * @param code 股票代码
     */
  /*  @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HistoryKl>> add(@RequestParam("code") String code) {
        log.info("subscribe code: {}", code);
        StocksBaseUs stocksBase = stocksBaseUsService.getByCode(code);
        if (stocksBase != null) {
            log.info("subscribe already stocksBase: {}", stocksBase);
            return CommonResult.failed("股票已经订阅过了");
        }
        historicalDataComponent.requestStockInfo(code);
        historicalDataComponent.requestHistoricalData(code);
        return CommonResult.success(new ArrayList<>());
    }*/

    /**
     * 增加订阅一个股票的历史数据
     *
     * @param code 股票代码
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HistoryKl>> add(@RequestParam("code") String code) {
        log.info("subscribe code: {}", code);
        StocksBaseUs stocksBase = stocksBaseUsService.getByCode(code);
        if (stocksBase != null) {
            log.info("subscribe already stocksBase: {}", stocksBase);
            return CommonResult.failed("股票已经订阅过了");
        }
        String conId = historicalDataWebApiComponent.requestStockInfo(code);
        historicalDataWebApiComponent.requestHistoricalData(conId);
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
    public CommonResult<HistoryKLUSResp> list(@RequestParam("code") String code,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate) {
        log.info("code: {}, startDate: {}, endDate: {}", code, startDate, endDate);
        HistoryKLUSResp resp = new HistoryKLUSResp();
        resp.setCode(code);
        List<StocksHistoryKlUs> historyKlList = stocksHistoryKlUsService.getHistoryKL(code, startDate, endDate);
        if (CollectionUtil.isNotEmpty(historyKlList)) {
            String name = historyKlList.get(0).getName();
            resp.setName(name);
            resp.setHistoryKlList(historyKlList);
        }
        return CommonResult.success(resp);
    }
}

