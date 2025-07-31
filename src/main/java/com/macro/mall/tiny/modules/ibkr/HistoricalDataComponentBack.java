package com.macro.mall.tiny.modules.ibkr;

import com.ib.client.Bar;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDescription;
import com.ib.client.ContractDetails;
import com.ib.client.Decimal;
import com.ib.client.DeltaNeutralContract;
import com.ib.client.DepthMktDataDescription;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Scope("prototype")
public class HistoricalDataComponentBack implements EWrapper, EReaderSignal {
    private EClientSocket client;
    private int nextReqId = 1;

    public HistoricalDataComponentBack() {
        // Create EClientSocket with correct parameters
        client = new EClientSocket(this, this);
    }

    @PostConstruct
    public void init(){
        client = new EClientSocket(this, this);
        // Connect to TWS (port 7497 for paper trading, 7496 for live)
        client.eConnect("127.0.0.1", 7496, 0);
        // Start the message reader thread
        EReader reader = new EReader(client, this);
        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                this.waitForSignal();
                try {
                    reader.processMsgs();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("Error processing messages: ", e);
                }
            }
        }).start();
        // Wait for connection to stabilize
        try {
            Thread.sleep(2000);
            log.info("Connection status: {}", client.isConnected() ? "Connected" : "Not connected");
        } catch (InterruptedException e) {
            log.error("Error while waiting for connection: ", e);
        }
    }


    @PostConstruct
    public void connect() {

        // Connect to TWS (port 7497 for paper trading, 7496 for live)
        client.eConnect("127.0.0.1", 7496, 0);
        // Start the message reader thread
        EReader reader = new EReader(client, this);
        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
                this.waitForSignal();
                try {
                    reader.processMsgs();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("Error processing messages: ", e);
                }
            }
        }).start();
        // Wait for connection to stabilize
        try {
            Thread.sleep(2000);
            log.info("Connection status: {}", client.isConnected() ? "Connected" : "Not connected");
        } catch (InterruptedException e) {
            log.error("Error while waiting for connection: ", e);
        }
    }

    public static void main(String[] args) {
        HistoricalDataComponentBack example = new HistoricalDataComponentBack();
        example.connect();
        if (example.client.isConnected()) {
            example.requestHistoricalData();
            // Keep program running to receive data
            try {
                Thread.sleep(10000); // Wait 10 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            log.error("Failed to connect to TWS");
        }
    }

    public void requestHistoricalData() {
        // Define the contract (e.g., AAPL stock)
        Contract contract = new Contract();
        contract.symbol("AAPL");
        contract.secType("STK");// "STK" 是 股票（Stock）的缩写，表示这是一个普通股票。
        contract.exchange("SMART"); // 指定证券的交易所或路由方式,"SMART" 是 Interactive Brokers 提供的智能路由机制，它会自动选择最佳交易所（例如 NASDAQ、NYSE）来获取数据或执行交易。
        contract.currency("USD");//指定证券的交易货币。

        // Request delayed data (for free accounts)
        client.reqMarketDataType(3);
        log.info("Sending historical data request for AAPL...");
        // Request 1 day of daily bars
        client.reqHistoricalData(nextReqId++, contract, "20150728 00:00:00", "3 D", "1 day",
                "TRADES", 1, 1, false, null);// "TRADES": 成交价（最常用）
        //useRTH 指定是否仅限常规交易时间,1：只返回常规交易时间,0：包括盘前和盘后数据
        //formatDate 指定返回数据的日期格式,1：返回标准日期格式,2：返回 Unix 时间戳（秒数）
        //keepUpToDate指定是否持续更新历史数据
    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        log.info("Historical Data: Date={}, Open={}, High={}, Low={}, Close={}, Volume={}", bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume());
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        log.info("Historical Data Request Completed: reqId={}, Start={}, End={}", reqId, startDateStr, endDateStr);
        client.eDisconnect();
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
    public void contractDetails(int reqId, ContractDetails contractDetails) {
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

    @Override
    public void issueSignal() {
        log.info("issueSignal");
    }

    @Override
    public void waitForSignal() {
        log.info("waitForSignal");
    }


}
