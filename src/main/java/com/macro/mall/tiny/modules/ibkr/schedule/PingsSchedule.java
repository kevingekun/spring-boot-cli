package com.macro.mall.tiny.modules.ibkr.schedule;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 定时Ping IBKR服务器
 */
@Slf4j
@Component
public class PingsSchedule {

    public static volatile Boolean ibkrConnected = false;
    public static volatile Boolean ibkrAuthenticated = false;

    @Scheduled(cron = "0 0/1 * ? * *")
    public void ping() {
        log.info("prevent the session from ending start");
        String url = "http://localhost:50000/v1/api/tickle";
        String result = HttpUtil.post(url, new HashMap<>());
        log.info("result = {}", result);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject iServer = jsonObject.getJSONObject("iserver");
        JSONObject authStatus = iServer.getJSONObject("authStatus");
        Boolean authenticated = authStatus.getBool("authenticated");
        Boolean connected = authStatus.getBool("connected");
        ibkrAuthenticated = authenticated;
        ibkrConnected = connected;
        log.info("prevent the session from ending end");
    }


}
