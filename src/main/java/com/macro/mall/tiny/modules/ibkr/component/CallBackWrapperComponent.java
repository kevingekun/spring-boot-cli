package com.macro.mall.tiny.modules.ibkr.component;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.Decimal;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.FamilyCode;
import com.ib.client.HistogramEntry;
import com.ib.client.HistoricalSession;
import com.ib.client.HistoricalTick;
import com.ib.client.HistoricalTickBidAsk;
import com.ib.client.HistoricalTickLast;
import com.ib.client.NewsProvider;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.PriceIncrement;
import com.ib.client.SoftDollarTier;
import com.ib.client.TickAttrib;
import com.ib.client.TickAttribBidAsk;
import com.ib.client.TickAttribLast;
import com.macro.mall.tiny.modules.ibkr.model.StocksHistoryKlUs;
import com.macro.mall.tiny.modules.ibkr.service.StocksBaseUsService;
import com.macro.mall.tiny.modules.ibkr.service.StocksHistoryKlUsService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Slf4j
@Component
public class CallBackWrapperComponent implements EWrapper {
    private final StocksBaseUsService stocksBaseUsService;
    private final StocksHistoryKlUsService stocksHistoryKlUsService;

    public CallBackWrapperComponent(StocksBaseUsService stocksBaseUsService, StocksHistoryKlUsService stocksHistoryKlUsService) {
        this.stocksBaseUsService = stocksBaseUsService;
        this.stocksHistoryKlUsService = stocksHistoryKlUsService;
    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        String stockCode = HistoricalDataComponent.reqIdMap.get(reqId);
        log.info("Historical Data: Date={}, Open={}, High={}, Low={}, Close={}, Volume={}", bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume());
        StocksHistoryKlUs stocksHistoryKlUs = new StocksHistoryKlUs();
        stocksHistoryKlUs.setCode(stockCode);
        // 将字符串 20150723 转换为 date
        Date date;
        try {
            date = new SimpleDateFormat("yyyyMMdd").parse(bar.time());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date now = new Date();
        stocksHistoryKlUs.setDataTime(date);
        stocksHistoryKlUs.setOpenPrice(BigDecimal.valueOf(bar.open()));
        stocksHistoryKlUs.setHighPrice(BigDecimal.valueOf(bar.high()));
        stocksHistoryKlUs.setLowPrice(BigDecimal.valueOf(bar.low()));
        stocksHistoryKlUs.setClosePrice(BigDecimal.valueOf(bar.close()));
        stocksHistoryKlUs.setUpdateDate(now);
        stocksHistoryKlUs.setCreateDate(now);
        try {
            //判断 date 是不是当天
            if (date.equals(now)) {
                // 是当天，更新数据
                stocksHistoryKlUsService.saveOrUpdateData(stocksHistoryKlUs);
            } else {
                stocksHistoryKlUsService.save(stocksHistoryKlUs);
            }
        } catch (Exception e) {
            log.error("save stocksHistoryKlUs error");
        }
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        log.info("Contract Details: {}", contractDetails);
        // 股票名称
        String stockName = contractDetails.longName();
        // 股票代码
        String stockCode = contractDetails.contract().symbol();
        stocksBaseUsService.saveStocksBaseUs(stockCode, stockName);
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        log.info("Historical Data Request Completed: reqId={}, Start={}, End={}", reqId, startDateStr, endDateStr);
    }

    @Override
    public void error(Exception e) {
        log.error("Exception: ", e);
    }

    @Override
    public void error(String str) {
        log.error("Error: {}", str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        log.error("Error: id={}, code={}, msg={}, advanced={}", id, errorCode, errorMsg, advancedOrderRejectJson);
    }

    @Override
    public void connectAck() {
        log.info("Connected to TWS");
    }

    @Override
    public void connectionClosed() {
        log.info("Connection closed");
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        log.info("Market Data Type: {} (1=Live, 2=Frozen, 3=Delayed, 4=Delayed Frozen)", marketDataType);
    }

    // Other EWrapper methods (empty implementations)
    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
    }

    @Override
    public void tickSize(int tickerId, int field, Decimal size) {
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {
    }

    @Override
    public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
    }

    @Override
    public void openOrderEnd() {
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
    }

    @Override
    public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
    }

    @Override
    public void updateAccountTime(String timeStamp) {
    }

    @Override
    public void accountDownloadEnd(String accountName) {
    }

    @Override
    public void nextValidId(int orderId) {
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
    }

    @Override
    public void contractDetailsEnd(int reqId) {
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
    }

    @Override
    public void execDetailsEnd(int reqId) {
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, Decimal size, boolean isSmartDepth) {
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
    }

    @Override
    public void managedAccounts(String accountsList) {
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
    }

    @Override
    public void scannerParameters(String xml) {
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
    }

    @Override
    public void scannerDataEnd(int reqId) {
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {
    }

    @Override
    public void currentTime(long time) {
    }

    @Override
    public void fundamentalData(int reqId, String data) {
    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
    }

    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {
    }

    @Override
    public void positionEnd() {
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
    }

    @Override
    public void accountSummaryEnd(int reqId) {
    }

    @Override
    public void verifyMessageAPI(String apiData) {
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos, double avgCost) {
    }

    @Override
    public void positionMultiEnd(int reqId) {
    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {
    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast, String exchange, String specialConditions) {
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
    }

    @Override
    public void completedOrdersEnd() {
    }

    @Override
    public void replaceFAEnd(int reqId, String text) {
    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {
    }

    @Override
    public void wshEventData(int reqId, String dataJson) {
    }

    @Override
    public void historicalSchedule(int i, String s, String s1, String s2, List<HistoricalSession> list) {

    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
    }

    @Override
    public void histogramData(int i, List<HistogramEntry> list) {

    }

    @Override
    public void historicalDataUpdate(int i, Bar bar) {

    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
    }

    @Override
    public void historicalTicks(int i, List<HistoricalTick> list, boolean b) {

    }

    @Override
    public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean b) {

    }

    @Override
    public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean b) {

    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {
    }

}
