package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTSPI_Conn;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ConnCallBack implements FTSPI_Conn {

    @Override
    public void onInitConnect(FTAPI_Conn client, long errCode, String desc) {
        log.info("Qot onInitConnect: ret={} desc={} connID={}", errCode, desc, client.getConnectID());
        if (errCode != 0) {
            log.error("Qot initConnect failed: ret={} desc={}", errCode, desc);
            throw new RuntimeException("Qot initConnect failed");
        }
        QotInit.isInit = true;
        log.info("Qot initConnected");
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        log.info("Qot onDisConnect: {}", errCode);
    }

}
