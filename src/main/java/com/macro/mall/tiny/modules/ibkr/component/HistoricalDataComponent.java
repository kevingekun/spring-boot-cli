package com.macro.mall.tiny.modules.ibkr.component;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Slf4j
@Component
public class HistoricalDataComponent {
    private EClientSocket client;
    private int nextReqId = 1;
    public static final HashMap<Integer, String> reqIdMap = new HashMap<>(8);

    private final CallBackReaderComponent callBackReaderComponent;
    private final CallBackWrapperComponent callBackWrapperComponent;

    public HistoricalDataComponent(CallBackReaderComponent callBackReaderComponent, CallBackWrapperComponent callBackWrapperComponent) {
        this.callBackReaderComponent = callBackReaderComponent;
        this.callBackWrapperComponent = callBackWrapperComponent;
    }

    @PostConstruct
    public void init() {
        client = new EClientSocket(callBackWrapperComponent, callBackReaderComponent);
        connect();
    }

    public void connect() {
        // Connect to TWS (port 7497 for paper trading, 7496 for live)
        client.eConnect("127.0.0.1", 7496, 0);
        // Start the message reader thread
        EReader reader = new EReader(client, callBackReaderComponent);
        reader.start();
        new Thread(() -> {
            while (client.isConnected()) {
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

    public void requestStockInfo(String symbol) {
        // 定义股票合约
        Contract contract = new Contract();
        contract.symbol(symbol.toUpperCase()); // 股票代码，例如 "AAPL"
        contract.secType("STK"); // 证券类型：股票
        contract.exchange("SMART"); // 交易所：智能路由
        contract.currency("USD"); // 货币：美元

        log.info("正在发送股票信息请求: {}", symbol);
        client.reqContractDetails(nextReqId++, contract);
    }

    public void requestHistoricalData(String code) {
        // Define the contract (e.g., AAPL stock)
        Contract contract = new Contract();
        contract.symbol(code);
        contract.secType("STK");// "STK" 是 股票（Stock）的缩写，表示这是一个普通股票。
        contract.exchange("SMART"); // 指定证券的交易所或路由方式,"SMART" 是 Interactive Brokers 提供的智能路由机制，它会自动选择最佳交易所（例如 NASDAQ、NYSE）来获取数据或执行交易。
        contract.currency("USD");//指定证券的交易货币。

        // Request delayed data (for free accounts)
        client.reqMarketDataType(3);
        log.info("Sending historical data request for {}...", code);
        int count = 0;
        while (!this.client.isConnected()) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                log.error("Qot getHistoryKL sleep error", e);
            }
            if (count > 50) {
                throw new RuntimeException("Qot getHistoryKL init failed");
            }
        }
        //获取当前日期
        LocalDate now = LocalDate.now();
        LocalDate localDate = LocalDate.of(2011, 1, 1);
        boolean running = true;
        if (this.client.isConnected()) {
            while (running) {
                // Request 1 day of daily bars
                int id = nextReqId++;
                //将业务对应 code 存入对应静态数据中，用于后续回调方法
                reqIdMap.put(id, code);
                String endTime = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + " 00:00:00 UTC";
                log.info(endTime);
                //useRTH 指定是否仅限常规交易时间,1：只返回常规交易时间,0：包括盘前和盘后数据
                //formatDate 指定返回数据的日期格式,1：返回标准日期格式,2：返回 Unix 时间戳（秒数）
                //keepUpToDate指定是否持续更新历史数据
                client.reqHistoricalData(id, contract, endTime, "365 D", "1 day",
                        "TRADES", 1, 1, false, null);// "TRADES": 成交价（最常用）
                //向后推一年
                localDate = localDate.plusYears(1);
                if (localDate.isAfter(now.plusYears(1))) {
                    running = false;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error("查询历史 k 线 sleep 异常", e);
                    running = false;
                }
            }
        }

    }

    public void getLastKL(String code) {
        // Define the contract (e.g., AAPL stock)
        Contract contract = new Contract();
        contract.symbol(code);
        contract.secType("STK");// "STK" 是 股票（Stock）的缩写，表示这是一个普通股票。
        contract.exchange("SMART"); // 指定证券的交易所或路由方式,"SMART" 是 Interactive Brokers 提供的智能路由机制，它会自动选择最佳交易所（例如 NASDAQ、NYSE）来获取数据或执行交易。
        contract.currency("USD");//指定证券的交易货币。

        // Request delayed data (for free accounts)
        client.reqMarketDataType(3);
        log.info("Sending historical data request for {}...", code);
        int count = 0;
        while (!this.client.isConnected()) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                log.error("Qot getHistoryKL sleep error", e);
            }
            if (count > 50) {
                throw new RuntimeException("Qot getHistoryKL init failed");
            }
        }
        //获取当前日期
        if (this.client.isConnected()) {
            // Request 1 day of daily bars
            int id = nextReqId++;
            //将业务对应 code 存入对应静态数据中，用于后续回调方法
            reqIdMap.put(id, code);
            LocalDate localDate = LocalDate.now();
            String endTime = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + " 00:00:00 UTC";
            log.info(endTime);
            //useRTH 指定是否仅限常规交易时间,1：只返回常规交易时间,0：包括盘前和盘后数据
            //formatDate 指定返回数据的日期格式,1：返回标准日期格式,2：返回 Unix 时间戳（秒数）
            //keepUpToDate指定是否持续更新历史数据
            client.reqHistoricalData(id, contract, endTime, "1 D", "1 day",
                    "TRADES", 1, 1, false, null);// "TRADES": 成交价（最常用）
        }
    }

}
