package com.macro.mall.tiny.modules.futu.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.futu.HistoryHandler;
import com.macro.mall.tiny.futu.HistoryKL;
import com.macro.mall.tiny.modules.futu.response.HistoryKLResp;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author macro
 * @since 2025-07-23
 */
@RestController
@RequestMapping("/historyKl")
@AllArgsConstructor
public class HistoryKlController {

    private final HistoryKlService historyKlService;
    private final HistoryKL historyKL;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody HistoryKl historyKl) {
        boolean success = historyKlService.save(historyKl);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody HistoryKl historyKl) {
        historyKl.setId(id);
        boolean success = historyKlService.updateById(historyKl);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = historyKlService.removeById(id);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        boolean success = historyKlService.removeByIds(ids);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HistoryKl>> add(@RequestParam("code") String code) {
        historyKL.getHistoryKL(code, null);
        return CommonResult.success(new ArrayList<>());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HistoryKLResp> list(@RequestParam("code") String code) {
        HistoryKLResp resp = new HistoryKLResp();
        resp.setCode(code);
        List<HistoryKl> historyKlList = historyKlService.getHistoryKL(code);
        if(CollectionUtil.isNotEmpty(historyKlList)){
            String name = historyKlList.get(0).getName();
            resp.setName(name);
            resp.setHistoryKlList(historyKlList);
        }
        return CommonResult.success(resp);
    }
}

