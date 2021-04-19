package com.cy.tradingbot.services.log;

import com.cy.tradingbot.component.TimeCalculator;
import com.cy.tradingbot.domain.entity.Log;
import com.cy.tradingbot.dto.LogDTO;
import com.cy.tradingbot.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    @Autowired
    private LogRepository logRepository;

    public void write(String reason) {
        Log log = new Log();
        log.setReason(reason);
        log.setDateTime(TimeCalculator.now());

        logRepository.save(log);
    }

    public List<LogDTO> getAll(long userId) {
        LocalDateTime end = TimeCalculator.now().plusDays(1);
        LocalDateTime start = LocalDateTime.of(end.toLocalDate(), LocalTime.of(0, 0, 0)).minusDays(2);

        return logRepository.findAllByUserIdAndDateTimeBetweenOrderByDateTimeDesc(userId, start, end).stream()
                .map(Log::toDTO)
                .collect(Collectors.toList());
    }
}
