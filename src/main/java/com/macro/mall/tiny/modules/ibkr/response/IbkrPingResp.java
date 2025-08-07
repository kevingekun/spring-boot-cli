package com.macro.mall.tiny.modules.ibkr.response;

import lombok.Data;

@Data
public class IbkrPingResp {
    private Boolean connected;
    private Boolean authenticated;
}
