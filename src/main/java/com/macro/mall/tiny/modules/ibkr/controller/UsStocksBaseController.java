package com.macro.mall.tiny.modules.ibkr.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ibkr.model.StocksBaseUs;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 股票基础信息 前端控制器
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@RestController
@RequestMapping("/us/stocksBase")
public class UsStocksBaseController {

    @Autowired
    public StocksBaseUsService stocksBaseUsService;

    /**
     * 获取所有股票基础信息
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StocksBaseUs>> listAll() {
        List<StocksBaseUs> stocksBaseList = stocksBaseUsService.listAllOfOrder();
        return CommonResult.success(stocksBaseList);
    }
}

