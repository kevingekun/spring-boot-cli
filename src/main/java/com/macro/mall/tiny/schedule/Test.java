package com.macro.mall.tiny.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Test {

    //每天下午 5 点执行
    @Scheduled(cron = "0 0 17 * * ?")
    public void test(){

    }
}
