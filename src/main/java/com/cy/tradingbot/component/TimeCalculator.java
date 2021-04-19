package com.cy.tradingbot.component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeCalculator {
    public static LocalDateTime closingTime() {
        LocalDateTime now = now();

        LocalTime clock9 = LocalTime.of(9, 0, 0, 0);

        LocalDateTime closingTime = LocalDateTime.of(now.toLocalDate(), clock9);

        if (now.isAfter(closingTime)) {
            closingTime = closingTime.plusDays(1);
        }

        return closingTime;
    }

    public static LocalDateTime now() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }
}
