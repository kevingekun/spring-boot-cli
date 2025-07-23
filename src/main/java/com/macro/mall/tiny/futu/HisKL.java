package com.macro.mall.tiny.futu;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HisKL implements FTSPI_Qot, FTSPI_Conn {
    FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

    public HisKL() {
        qot.setClientInfo("javaclient", 1);  //设置客户端信息
        qot.setConnSpi(this);  //设置连接回调
        qot.setQotSpi(this);   //设置交易回调
    }

    public void start() {
        qot.initConnect("127.0.0.1", (short)11111, false);
    }

    @Override
    public void onInitConnect(FTAPI_Conn client, long errCode, String desc)
    {
        System.out.printf("Qot onInitConnect: ret=%b desc=%s connID=%d\n", errCode, desc, client.getConnectID());
        if (errCode != 0)
            return;

        //股票市场以及股票代码
        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
                .setCode("00700")//腾讯
                .build();

        QotRequestHistoryKL.C2S c2s = QotRequestHistoryKL.C2S.newBuilder()
                .setRehabType(QotCommon.RehabType.RehabType_Forward_VALUE)
                .setKlType(QotCommon.KLType.KLType_Day_VALUE)
                .setSecurity(sec) // //股票市场以及股票代码
                .setMaxAckKLNum(10)
                .setNextReqKey(ByteString.copyFromUtf8("9iEAABEYAgAAY2FoAAAAAA=="))
                .setBeginTime("2025-06-15")
                .setEndTime("2025-07-22")
                .setNeedKLFieldsFlag(520)
                .build();
        QotRequestHistoryKL.Request req = QotRequestHistoryKL.Request.newBuilder().setC2S(c2s).build();
        int seqNo = qot.requestHistoryKL(req);
        System.out.printf("Send QotRequestHistoryKL: %d\n", seqNo);
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        System.out.printf("Qot onDisConnect: %d\n", errCode);
    }

    @Override
    public void onReply_RequestHistoryKL(FTAPI_Conn client, int nSerialNo, QotRequestHistoryKL.Response rsp) {
        if (rsp.getRetType() != 0) {
            System.out.printf("QotRequestHistoryKL failed: %s\n", rsp.getRetMsg());
        }
        else {
            try {
                String json = JsonFormat.printer().print(rsp);
                JSONObject jsonObject = JSONUtil.parseObj(json);
                log.info("Receive QotRequestHistoryKL: {}", jsonObject);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FTAPI.init();
        HisKL qot = new HisKL();
        qot.start();

        while (true) {
            try {
                Thread.sleep(1000 * 600);
            } catch (InterruptedException exc) {

            }
        }
    }
}


