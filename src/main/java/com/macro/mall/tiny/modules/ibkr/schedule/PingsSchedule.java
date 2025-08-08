package com.macro.mall.tiny.modules.ibkr.schedule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.macro.mall.tiny.modules.ibkr.component.ProcessManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 定时Ping IBKR服务器
 */
@Slf4j
@Component
@AllArgsConstructor
public class PingsSchedule {

    private final ProcessManager processManager;

    public static volatile Boolean ibkrConnected = false;
    public static volatile Boolean ibkrAuthenticated = false;

    /**
     * 定时检查 IBKR Web API 连接状态，若断开则重启
     */
    @Scheduled(cron = "0 0/5 * ? * *")
    public void restartIbkrWebApi() {
        if (!ibkrConnected) {
            processManager.restartIbkrWebApi();
            return;
        }
        if (!ibkrAuthenticated) {
            processManager.restartIbkrWebApi();
        }
    }

    /**
     * 定时Ping IBKR服务器，检查连接状态
     */
    @Scheduled(cron = "0 0/1 * ? * *")
    public void ping() {
        log.info("prevent the session from ending start");
        String url = "http://localhost:50000/v1/api/tickle";
        String result;
        try {
            result = HttpUtil.post(url, new HashMap<>());
        } catch (Exception e) {
            log.error("IBKR web api 连接失败，服务异常", e);
            return;
        }
        log.info("result = {}", result);
        if (StrUtil.isBlank(result)) {
            log.error("IBKR web api 连接失败，响应为空");
            ibkrConnected = false;
            ibkrAuthenticated = false;
            return;
        }
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result);
            JSONObject iServer = jsonObject.getJSONObject("iserver");
            JSONObject authStatus = iServer.getJSONObject("authStatus");
            Boolean authenticated = authStatus.getBool("authenticated");
            Boolean connected = authStatus.getBool("connected");
            ibkrAuthenticated = authenticated;
            ibkrConnected = connected;
            log.info("prevent the session from ending end");
            if (!connected) {
                log.error("IBKR web api 连接失败，连接失败");
                return;
            }
            if (!authenticated) {
                log.info("IBKR连接成功，但未认证");
                // 重新认证
                String authUrl = "http://localhost:50000/v1/api/iserver/auth/ssodh/init";
                JSONObject body = new JSONObject();
                body.put("publish", true);
                body.put("compete", true);
                String authResult = HttpUtil.post(authUrl, body.toString());
                log.info("IBKR连接成功，重新认证结果 = {}", authResult);
            }
        } catch (Exception e) {
            log.error("IBKR web api 连接失败，解析响应失败", e);
            ibkrConnected = false;
            ibkrAuthenticated = false;
        }

    }


}
