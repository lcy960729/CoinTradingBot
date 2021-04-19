package com.cy.tradingbot.repository;

import com.cy.tradingbot.domain.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByUserIdAndDateTimeBetweenOrderByDateTimeDesc(long userId, LocalDateTime startTime, LocalDateTime endTime);
}
