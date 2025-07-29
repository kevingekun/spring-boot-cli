package com.macro.mall.tiny.modules.futu.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.futu.component.HistoryKLComponent;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.response.HistoryKLResp;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
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
@RestController
@RequestMapping("/historyKl")
@AllArgsConstructor
public class HistoryKlController {

    private final HistoryKlService historyKlService;
    private final ApplicationContext applicationContext;

    /**
     * 增加一个股票的历史数据
     *
     * @param code 股票代码
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HistoryKl>> add(@RequestParam("code") String code) {
        HistoryKLComponent historyKLComponent = applicationContext.getBean(HistoryKLComponent.class);
        historyKLComponent.getHistoryKL(code, null);
        return CommonResult.success(new ArrayList<>());
    }

    /**
     * 根据 code 查询全部历史数据
     *
     * @param code 股票代码
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HistoryKLResp> list(@RequestParam("code") String code) {
        HistoryKLResp resp = new HistoryKLResp();
        resp.setCode(code);
        List<HistoryKl> historyKlList = historyKlService.getHistoryKL(code);
        if (CollectionUtil.isNotEmpty(historyKlList)) {
            String name = historyKlList.get(0).getName();
            resp.setName(name);
            resp.setHistoryKlList(historyKlList);
        }
        return CommonResult.success(resp);
    }
}

