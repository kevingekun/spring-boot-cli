package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotGetKL;
import com.futu.openapi.pb.QotSub;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class QotDemo implements FTSPI_Qot, FTSPI_Conn {
    FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

    public QotDemo() {
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

        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
                .build();
        QotSub.C2S c2s = QotSub.C2S.newBuilder()
                .addSecurityList(sec)
                .addSubTypeList(QotCommon.SubType.SubType_KL_Day_VALUE)
                .setIsSubOrUnSub(true)
                .build();
        QotSub.Request req = QotSub.Request.newBuilder().setC2S(c2s).build();
        int seqNo = qot.sub(req);
//        System.out.printf("Send QotSub: %d\n", seqNo);
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        System.out.printf("Qot onDisConnect: %d\n", errCode);
    }

    @Override
    public void onReply_Sub(FTAPI_Conn client, int nSerialNo, QotSub.Response rsp) {
        System.out.printf("Reply: QotSub: %d  %s\n", nSerialNo, rsp.toString());

        if (rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE)
            return;

        QotCommon.Security sec = QotCommon.Security.newBuilder()
                .setMarket(QotCommon.QotMarket.QotMarket_HK_Security_VALUE)
                .setCode("00700")
                .build();
        QotGetKL.C2S c2s = QotGetKL.C2S.newBuilder()
                .setSecurity(sec)
                .setKlType(QotCommon.KLType.KLType_Day_VALUE)
                .setRehabType(QotCommon.RehabType.RehabType_Forward_VALUE)
                .setReqNum(1)
                .build();
        QotGetKL.Request req = QotGetKL.Request.newBuilder().setC2S(c2s).build();
        int seqNo = qot.getKL(req);
        System.out.printf("Send QotGetKL: %d\n", seqNo);
    }

    @Override
    public void onReply_GetKL(FTAPI_Conn client, int nSerialNo, QotGetKL.Response rsp) {
        if (rsp.getRetType() != 0) {
            System.out.printf("QotGetKL failed: %s\n", rsp.getRetMsg());
        }
        else {
            try {
                String json = JsonFormat.printer().print(rsp);
                System.out.printf("Receive QotGetKL: %s\n", json);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FTAPI.init();
        QotDemo qot = new QotDemo();
        qot.start();

        while (true) {
            try {
                Thread.sleep(1000 * 600);
            } catch (InterruptedException exc) {

            }
        }
    }
}

