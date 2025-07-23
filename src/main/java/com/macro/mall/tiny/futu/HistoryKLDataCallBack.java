package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 历史K线回调处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class HistoryKLDataCallBack implements FTSPI_Qot {

    private final HistoryKlService historyKlService;
    private final HistoryKL historyKL;

    @Override
    public void onReply_RequestHistoryKL(FTAPI_Conn client, int nSerialNo, QotRequestHistoryKL.Response rsp) {
        if (rsp.getRetType() != 0) {
            log.error("QotRequestHistoryKL failed: {}", rsp.getRetMsg());
        } else {
            log.info("Receive QotRequestHistoryKL: {}", rsp);
            String code = rsp.getS2C().getSecurity().getCode();
            String name = rsp.getS2C().getName();
            List<QotCommon.KLine> klListList = rsp.getS2C().getKlListList();
            List<HistoryKl> historyKlList = new ArrayList<>();
            for (QotCommon.KLine kLine : klListList) {
                double openPrice = kLine.getOpenPrice();
                double closePrice = kLine.getClosePrice();
                double highPrice = kLine.getHighPrice();
                double lowPrice = kLine.getLowPrice();
                double changeRate = kLine.getChangeRate();
                double timestamp = kLine.getTimestamp();
                HistoryKl historyKl = new HistoryKl();
                //timestamp 为秒级时间戳,转换为 date
                historyKl.setDataTime(new Date((long) (timestamp * 1000)));
                historyKl.setOpenPrice(new BigDecimal(openPrice).setScale(2, RoundingMode.HALF_UP));
                historyKl.setClosePrice(new BigDecimal(closePrice).setScale(2, RoundingMode.HALF_UP));
                historyKl.setHighPrice(new BigDecimal(highPrice).setScale(2, RoundingMode.HALF_UP));
                historyKl.setLowPrice(new BigDecimal(lowPrice).setScale(2, RoundingMode.HALF_UP));
                historyKl.setChangeRate(new BigDecimal(changeRate).setScale(2, RoundingMode.HALF_UP));
                historyKl.setCode(code);
                historyKl.setName(name);
                historyKl.setCreateDate(new Date());
                historyKl.setUpdateDate(new Date());
                historyKlList.add(historyKl);
            }
            //批量插入
            historyKlService.saveBatch(historyKlList);
            log.info("QotRequestHistoryKL success: {} {}", code, name);
            //递归调用
            ByteString nextReqKey = rsp.getS2C().getNextReqKey();
            if (!nextReqKey.isEmpty()) {
                historyKL.getHistoryKL(code, nextReqKey);
            }
        }
    }
}
