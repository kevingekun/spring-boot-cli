package com.macro.mall.tiny.modules.futu.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import java.util.List;
import org.springframework.web.bind.annotation.*;


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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody StocksBase stocksBase) {
        boolean success = stocksBaseService.save(stocksBase);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody StocksBase stocksBase) {
        stocksBase.setId(id.intValue());
        boolean success = stocksBaseService.updateById(stocksBase);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = stocksBaseService.removeById(id);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean success = stocksBaseService.removeByIds(ids);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }


    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<StocksBase>> listAll() {
        List<StocksBase> stocksBaseList = stocksBaseService.list();
        return CommonResult.success(stocksBaseList);
    }
}

