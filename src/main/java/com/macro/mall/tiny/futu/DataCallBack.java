package com.macro.mall.tiny.futu;

import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.QotCommon;
import com.futu.openapi.pb.QotRequestHistoryKL;
import com.google.protobuf.ByteString;
import com.macro.mall.tiny.modules.futu.model.HistoryKl;
import com.macro.mall.tiny.modules.futu.model.StocksBase;
import com.macro.mall.tiny.modules.futu.service.HistoryKlService;
import com.macro.mall.tiny.modules.futu.service.StocksBaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据回调处理
 */
@Slf4j
@Component
@AllArgsConstructor
public class DataCallBack implements FTSPI_Qot {

    private final HistoryKlService historyKlService;
    private final StocksBaseService stocksBaseService;
    private final HistoryKL historyKL;

    /**
     * 历史K线回调处理
     *
     * @param client    client
     * @param nSerialNo nSerialNo
     * @param rsp       rsp
     **/
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
                historyKL.getHistoryKL(code, nextReqKey);
            } else {
                //保存基础数据
                StocksBase stocksBase = new StocksBase();
                stocksBase.setCode(code);
                stocksBase.setName(name);
                stocksBase.setCreateDate(dateNow);
                stocksBaseService.save(stocksBase);
            }
        }
    }
}
