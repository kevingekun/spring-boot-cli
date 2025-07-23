package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn_Qot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 历史 k 线
 */
@Slf4j
@Component
public class QotInit {

    private final ConnCallBack connCallBack;
    private final DataCallBack dataCallBack;

    public static boolean isInit = false;

    public QotInit(ConnCallBack connCallBack, DataCallBack dataCallBack) {
        this.connCallBack = connCallBack;
        this.dataCallBack = dataCallBack;
    }

    @Getter
    public static FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

    @PostConstruct
    public void start() {
        FTAPI.init();
        qot.setClientInfo("javaclient", 1);  //设置客户端信息
        qot.setConnSpi(connCallBack);  //设置连接回调
        qot.setQotSpi(dataCallBack);   //设置交易回调
        qot.initConnect("127.0.0.1", (short) 11111, false);
    }
}


