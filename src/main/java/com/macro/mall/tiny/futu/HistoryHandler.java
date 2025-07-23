package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn_Qot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class HistoryHandler {

    private final HistoryKLConnCallBack historyKLConnCallBack;
    private final HistoryKLDataCallBack historyKLDataCallBack;

    public static boolean isInit = false;

    public HistoryHandler(HistoryKLConnCallBack historyKLConnCallBack, HistoryKLDataCallBack historyKLDataCallBack) {
        this.historyKLConnCallBack = historyKLConnCallBack;
        this.historyKLDataCallBack = historyKLDataCallBack;
    }

    @Getter
    public static FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

    @PostConstruct
    public void start() {
        FTAPI.init();
        qot.setClientInfo("javaclient", 1);  //设置客户端信息
        qot.setConnSpi(historyKLConnCallBack);  //设置连接回调
        qot.setQotSpi(historyKLDataCallBack);   //设置交易回调
        qot.initConnect("127.0.0.1", (short) 11111, false);
    }
}


