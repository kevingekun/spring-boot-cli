package com.macro.mall.tiny.modules.ibkr.response;

import com.macro.mall.tiny.modules.ibkr.model.StocksHistoryKlUs;
import lombok.Data;

import java.util.List;

@Data
public class HistoryKLUSResp {
    private String code;
    private String name;
    private List<StocksHistoryKlUs> historyKlList;
}
