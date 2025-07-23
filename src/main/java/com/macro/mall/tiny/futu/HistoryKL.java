package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HistoryKL {

    public void getHistoryKL(String code, ByteString nextReqKey) {
        if (!HistoryHandler.isInit) {
            log.error("Qot not init");
            return;
        }
        FTAPI_Conn_Qot qot = HistoryHandler.getQot();
        //股票市场以及股票代码
        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
                .setCode(code)//腾讯
                .build();

        QotRequestHistoryKL.C2S c2s = QotRequestHistoryKL.C2S.newBuilder()
                .setRehabType(QotCommon.RehabType.RehabType_Forward_VALUE)
                .setKlType(QotCommon.KLType.KLType_Day_VALUE)
                .setSecurity(sec) // //股票市场以及股票代码
                .setMaxAckKLNum(100)
                .setNextReqKey(nextReqKey == null ? ByteString.EMPTY : nextReqKey)
                .setBeginTime("2015-01-01")
                .setEndTime("2025-07-22")
                .setNeedKLFieldsFlag(527)
                .build();
        QotRequestHistoryKL.Request req = QotRequestHistoryKL.Request.newBuilder().setC2S(c2s).build();
        qot.requestHistoryKL(req);
    }
}
