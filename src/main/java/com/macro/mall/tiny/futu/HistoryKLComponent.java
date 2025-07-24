package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.Common;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class HistoryKLComponent implements FTSPI_Qot, FTSPI_Conn {

    private final HistoryKlService historyKlService;
    private final StocksBaseService stocksBaseService;

    private volatile boolean isInit = false;
    private final FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

    public HistoryKLComponent(HistoryKlService historyKlService, StocksBaseService stocksBaseService) {
        this.historyKlService = historyKlService;
        this.stocksBaseService = stocksBaseService;
    }

    @PostConstruct
    public void init() {
        FTAPI.init();
        qot.setClientInfo("javaclient", 1);  //设置客户端信息
        qot.setConnSpi(this);  //设置连接回调
        qot.setQotSpi(this);   //设置交易回调
        qot.initConnect("127.0.0.1", (short) 11111, false);
    }
    /**
     * 请求历史K线数据
     *
     * @param code       股票代码
     * @param nextReqKey nextReqKey
     **/
    public void getHistoryKL(String code, ByteString nextReqKey) {
        int count = 0;
        while (!isInit) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                log.error("Qot getHistoryKL sleep error", e);
            }
            if (count > 50) {
                isInit = true;
                throw new RuntimeException("Qot getHistoryKL init failed");
            }
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

    @Override
    public void onInitConnect(FTAPI_Conn client, long errCode, String desc) {
        log.info("Qot onInitConnect: ret={} desc={} connID={}", errCode, desc, client.getConnectID());
        if (errCode != 0) {
            log.error("Qot initConnect failed: ret={} desc={}", errCode, desc);
            throw new RuntimeException("Qot initConnect failed");
        }
        isInit = true;
        log.info("Qot initConnected");
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        log.info("Qot onDisConnect: {}", errCode);
    }

    /**
     * 历史K线回调处理
     *
     * @param client    client
     * @param nSerialNo nSerialNo
     * @param rsp       rsp
     **/
    @Override
    public void onReply_RequestHistoryKL(FTAPI_Conn client, int nSerialNo, QotRequestHistoryKL.Response rsp) {
        if (rsp.getRetType() != Common.RetType.RetType_Succeed_VALUE) {
            log.error("QotRequestHistoryKL failed: {}", rsp.getRetMsg());
            return;
        }
        log.info("Receive QotRequestHistoryKL: {}", rsp);
        String code = rsp.getS2C().getSecurity().getCode();
        String name = rsp.getS2C().getName();
        List<QotCommon.KLine> klListList = rsp.getS2C().getKlListList();
        List<HistoryKl> historyKlList = new ArrayList<>();
        Date dateNow = new Date();
        HistoryKl historyKlNow = null;
        for (QotCommon.KLine kLine : klListList) {
            double openPrice = kLine.getOpenPrice();
            double closePrice = kLine.getClosePrice();
            double highPrice = kLine.getHighPrice();
            double lowPrice = kLine.getLowPrice();
            double changeRate = kLine.getChangeRate();
            double timestamp = kLine.getTimestamp();
            HistoryKl historyKl = new HistoryKl();
            //timestamp 为秒级时间戳,转换为 date
            Date date = new Date((long) (timestamp * 1000));
            historyKl.setDataTime(date);
            historyKl.setOpenPrice(new BigDecimal(openPrice).setScale(2, RoundingMode.HALF_UP));
            historyKl.setClosePrice(new BigDecimal(closePrice).setScale(2, RoundingMode.HALF_UP));
            historyKl.setHighPrice(new BigDecimal(highPrice).setScale(2, RoundingMode.HALF_UP));
            historyKl.setLowPrice(new BigDecimal(lowPrice).setScale(2, RoundingMode.HALF_UP));
            historyKl.setChangeRate(new BigDecimal(changeRate).setScale(2, RoundingMode.HALF_UP));
            historyKl.setCode(code);
            historyKl.setName(name);
            historyKl.setUpdateDate(dateNow);
            LocalDate localDate = LocalDate.now();
            // localDate 与 date 比较是不是同一天
            LocalDate dateLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (dateLocalDate.equals(localDate)) {
                //当天数据单独处理
                historyKlNow = historyKl;
                continue;
            }
            historyKl.setCreateDate(dateNow);
            historyKlList.add(historyKl);
        }
        //批量插入
        historyKlService.saveBatch(historyKlList);
        if (historyKlNow != null) {
            //当天数据单独处理
            historyKlService.saveOrUpdateData(historyKlNow);
        }
        log.info("QotRequestHistoryKL success: {} {}", code, name);
        //递归调用
        ByteString nextReqKey = rsp.getS2C().getNextReqKey();
        if (!nextReqKey.isEmpty()) {
            getHistoryKL(code, nextReqKey);
        } else {
            //关闭连接
            qot.close();
            //保存基础数据
            StocksBase stocksBase = new StocksBase();
            stocksBase.setCode(code);
            stocksBase.setName(name);
            stocksBase.setCreateDate(dateNow);
            stocksBaseService.saveStocksBase(stocksBase);
        }
    }

}
