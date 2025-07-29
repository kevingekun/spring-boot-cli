package com.macro.mall.tiny.modules.futu.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
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
@RequestMapping("/stocksBase")
public class StocksBaseController {

    @Autowired
    public StocksBaseService stocksBaseService;

    /**
     * 获取所有股票基础信息
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StocksBase>> listAll() {
        List<StocksBase> stocksBaseList = stocksBaseService.list();
        return CommonResult.success(stocksBaseList);
    }
}

