package com.ucamp.coffee.common.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeUtil {
    public static String toUtcDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        return dateTime.atZone(ZoneOffset.UTC).toString();
    }
}
