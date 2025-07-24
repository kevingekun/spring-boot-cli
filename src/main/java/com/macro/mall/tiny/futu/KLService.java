package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@AllArgsConstructor
public class KLService {

    private final QotInit qotInit;

    /**
     * 请求历史K线数据
     *
     * @param code       股票代码
     * @param nextReqKey nextReqKey
     **/
    public void getHistoryKL(String code, DataCallBack dataCallBack, ByteString nextReqKey) {
        FTAPI_Conn_Qot qot = qotInit.getQot(dataCallBack);
        //股票市场以及股票代码
        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)//港股
                .setCode(code)//股票代码
                .build();
        //当天日期，格式 2025-07-22
        String endTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        QotRequestHistoryKL.C2S c2s = QotRequestHistoryKL.C2S.newBuilder()
                .setRehabType(QotCommon.RehabType.RehabType_Forward_VALUE)
                .setKlType(QotCommon.KLType.KLType_Day_VALUE)
                .setSecurity(sec) //股票市场以及股票代码
                .setMaxAckKLNum(100) //最大返回条数
                .setNextReqKey(nextReqKey == null ? ByteString.EMPTY : nextReqKey)//上一次返回的nextReqKey，首次请求可以为空
                .setBeginTime("2015-01-01")
                .setEndTime(endTime)
                .setNeedKLFieldsFlag(527) //需要返回的K线数据字段(527=512+8+4+2+1) https://openapi.futunn.com/futu-api-doc/quote/quote.html#481
                .build();
        QotRequestHistoryKL.Request req = QotRequestHistoryKL.Request.newBuilder().setC2S(c2s).build();
        //发送请求
        qot.requestHistoryKL(req);
    }

    /**
     * 获取最近一天的 k 线
     *
     * @param code 股票代码
     **/
    public void getLastDayKL(String code, DataCallBack dataCallBack) {
        FTAPI_Conn_Qot qot = qotInit.getQot(dataCallBack);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //股票市场以及股票代码
        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)//港股
                .setCode(code)//股票代码
                .build();
        //当天日期，格式 2025-07-22
        String endTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        QotRequestHistoryKL.C2S c2s = QotRequestHistoryKL.C2S.newBuilder()
                .setRehabType(QotCommon.RehabType.RehabType_Forward_VALUE)
                .setKlType(QotCommon.KLType.KLType_Day_VALUE)
                .setSecurity(sec) //股票市场以及股票代码
                .setMaxAckKLNum(1) //最大返回条数
                .setBeginTime(endTime)
                .setEndTime(endTime)
                .setNeedKLFieldsFlag(527) //需要返回的K线数据字段(527=512+8+4+2+1) https://openapi.futunn.com/futu-api-doc/quote/quote.html#481
                .build();
        QotRequestHistoryKL.Request req = QotRequestHistoryKL.Request.newBuilder().setC2S(c2s).build();
        //发送请求
        qot.requestHistoryKL(req);
    }
}
