package com.macro.mall.tiny.modules.ibkr.component;

import com.ib.client.EReaderSignal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallBackReaderComponent implements EReaderSignal {

    @Override
    public void issueSignal() {
        log.info("issueSignal");
    }

    @Override
    public void waitForSignal() {
        log.info("waitForSignal");
    }

}
