package com.macro.mall.tiny.modules.ibkr.component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macro.mall.tiny.modules.ibkr.model.StocksHistoryKlUs;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import com.macro.mall.tiny.modules.ibkr.service.StocksHistoryKlUsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通过 web api 查询股票信息，需要在本地安装运行 clientportal.gw
 * 参考：https://www.interactivebrokers.com/campus/ibkr-api-page/cpapi-v1/#gw-step-one
 * 注意：ibkr 账户需要开启数据订阅功能，否则clientportal.gw无法登录成功！！
 */
@Slf4j
@Component
@AllArgsConstructor
public class HistoricalDataWebApiComponent {

    private final StocksBaseUsService stocksBaseUsService;
    private final StocksHistoryKlUsService stocksHistoryKlUsService;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public String requestStockInfo(String symbol) {
        log.info("股票信息查询:{}", symbol);
        String upperCase = symbol.toUpperCase();
        String result = HttpUtil.get("http://localhost:50000/v1/api/iserver/secdef/search?symbol=" + upperCase + "&name=true");
        JSONArray jsonArray = JSONUtil.parseArray(result);
        if (!jsonArray.isEmpty()) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String symbol1 = jsonObject.getStr("symbol");
            String secType = jsonObject.getStr("secType");
            if (!StrUtil.equals(symbol1, upperCase) || !StrUtil.equals(secType, "STK")) {
                log.info("股票信息查询异常:{}，查询结果:{}", symbol, result);
                return null;
            }
            log.info("股票信息查询成功:{}，查询结果:{}", symbol, jsonObject);
            return jsonObject.getStr("conid");
        }
        return null;
    }

    public int requestHistoricalData(String conId) {
        log.info("股票历史数据查询:{}", conId);
        Date now = new Date();
        // 获取当前日期
        LocalDate today = LocalDate.now();
        //将 now 转换为 startTime 样式
        String format = simpleDateFormat.format(now);
        boolean running = true;
        int count = 0;
        while (running) {
            count ++;
            String sTime = format + "-18:30:00";
            String url = "http://localhost:50000/v1/api/iserver/marketdata/history?conid=" + conId + "&exchange=SMART&period=100d&bar=1d&startTime=" + sTime + "&outsideRth=false";
            String result = HttpUtil.get(url);
            log.info("股票历史数据查询成功:{}，查询结果:{}", conId, result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String stockCode = jsonObject.getStr("symbol");
            String stockName = jsonObject.getStr("text");
            //保存基础信息
            stocksBaseUsService.saveStocksBaseUs(stockCode, stockName);
            String startTime = jsonObject.getStr("startTime");
            // startTime 格式为 20250714-13:30:00，判断是否小于 2011 年 1 月 1 号
            if (StrUtil.isNotBlank(startTime) && startTime.compareTo("20110101-00:00:00") < 0) {
                running = false;
            }
            // startTime 加一天
            format = startTimePlus1Day(startTime, format);
            JSONArray data = jsonObject.getJSONArray("data");
            if (!data.isEmpty()) {
                List<StocksHistoryKlUs> list = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    Float o = jsonObject1.getFloat("o");
                    Float c = jsonObject1.getFloat("c");
                    Float h = jsonObject1.getFloat("h");
                    Float l = jsonObject1.getFloat("l");
                    //格式:1753795800000
                    Long t = jsonObject1.getLong("t");
                    StocksHistoryKlUs stocksHistoryKlUs = new StocksHistoryKlUs();
                    stocksHistoryKlUs.setCode(stockCode);
                    // 将毫秒转换为日期
                    Date date = new Date(t);
                    // 将 Date 转换为 LocalDate，只保留年月日
                    LocalDate localDate = date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    // 将 LocalDate 转换回 Date（时间部分为 00:00:00）
                    Date dateOnly = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    stocksHistoryKlUs.setDataTime(dateOnly);
                    stocksHistoryKlUs.setOpenPrice(BigDecimal.valueOf(o));
                    stocksHistoryKlUs.setHighPrice(BigDecimal.valueOf(h));
                    stocksHistoryKlUs.setLowPrice(BigDecimal.valueOf(l));
                    stocksHistoryKlUs.setClosePrice(BigDecimal.valueOf(c));
                    stocksHistoryKlUs.setUpdateDate(now);
                    stocksHistoryKlUs.setCreateDate(now);
                    try {
                        //判断 date 是不是当天
                        if (localDate.isEqual(today)) {
                            // 是当天，更新数据
                            stocksHistoryKlUsService.saveOrUpdateData(stocksHistoryKlUs);
                        } else {
                            list.add(stocksHistoryKlUs);
                        }
                    } catch (Exception e) {
                        log.error("save stocksHistoryKlUs error");
                    }
                }
                stocksHistoryKlUsService.saveBatch(list);
            } else {
                running = false;
            }
            if (count > 1000) {
                //避免无限循环
                running = false;
            }
        }
        log.info("股票历史数据查询完成:{}", conId);
        return count;
    }

    private static String startTimePlus1Day(String startTime, String format) {
        if (StrUtil.isNotBlank(startTime)) {
            try {
                Date parse = simpleDateFormat.parse(startTime.split("-")[0]);
                parse.setTime(parse.getTime() + 24 * 60 * 60 * 1000);
                format = simpleDateFormat.format(parse);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return format;
    }

    public void getLastKL(String code) {
        String conId = requestStockInfo(code);
        if (StrUtil.isNotBlank(conId)) {
            log.info("股票当天数据查询:{}", conId);
            Date now = new Date();
            // 获取当前日期
            LocalDate today = LocalDate.now();
            //将 now 转换为 startTime 样式
            String format = simpleDateFormat.format(now);
            String sTime = format + "-18:30:00";
            String url = "http://localhost:50000/v1/api/iserver/marketdata/history?conid=" + conId + "&exchange=SMART&period=2d&bar=1d&startTime=" + sTime + "&outsideRth=false";
            String result = HttpUtil.get(url);
            log.info("股票当天数据查询成功:{}，查询结果:{}", conId, result);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            String stockCode = jsonObject.getStr("symbol");
            JSONArray data = jsonObject.getJSONArray("data");
            if (!data.isEmpty()) {
                List<StocksHistoryKlUs> list = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    Float o = jsonObject1.getFloat("o");
                    Float c = jsonObject1.getFloat("c");
                    Float h = jsonObject1.getFloat("h");
                    Float l = jsonObject1.getFloat("l");
                    //格式:1753795800000
                    Long t = jsonObject1.getLong("t");
                    StocksHistoryKlUs stocksHistoryKlUs = new StocksHistoryKlUs();
                    stocksHistoryKlUs.setCode(stockCode);
                    // 将毫秒转换为日期
                    Date date = new Date(t);
                    // 将 Date 转换为 LocalDate，只保留年月日
                    LocalDate localDate = date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    // 将 LocalDate 转换回 Date（时间部分为 00:00:00）
                    Date dateOnly = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    stocksHistoryKlUs.setDataTime(dateOnly);
                    stocksHistoryKlUs.setOpenPrice(BigDecimal.valueOf(o));
                    stocksHistoryKlUs.setHighPrice(BigDecimal.valueOf(h));
                    stocksHistoryKlUs.setLowPrice(BigDecimal.valueOf(l));
                    stocksHistoryKlUs.setClosePrice(BigDecimal.valueOf(c));
                    stocksHistoryKlUs.setUpdateDate(now);
                    stocksHistoryKlUs.setCreateDate(now);
                    try {
                        //判断 date 是不是当天
                        if (date.equals(now)) {
                            // 是当天，更新数据
                            stocksHistoryKlUsService.saveOrUpdateData(stocksHistoryKlUs);
                        } else {
                            list.add(stocksHistoryKlUs);
                        }
                    } catch (Exception e) {
                        log.error("save stocksHistoryKlUs error");
                    }
                }
                stocksHistoryKlUsService.saveBatch(list);
            }
            log.info("股票当天数据查询完成:{}", conId);
        }
    }

}
