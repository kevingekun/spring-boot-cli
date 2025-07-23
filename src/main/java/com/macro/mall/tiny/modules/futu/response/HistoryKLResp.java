package com.macro.mall.tiny.modules.futu.response;

import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import lombok.Data;

import java.util.List;

@Data
public class HistoryKLResp {
    private String code;
    private String name;
    private List<HistoryKl> historyKlList;
}
