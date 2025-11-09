package com.ucamp.coffee.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@Component
public class TimeZoneConfig {
    @PostConstruct
    public void setupDefaultTimeZone() {
        ZoneId before = ZoneId.systemDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        ZoneId after = ZoneId.systemDefault();
        log.info("JVM Default TimeZone {}→{}", before, after);
    }
}
