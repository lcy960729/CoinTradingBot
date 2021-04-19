package com.cy.tradingbot.repository;

import com.cy.tradingbot.domain.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUserIdAndDateTimeBetweenOrderByDateTimeDesc(long userId, LocalDateTime startTime, LocalDateTime endTime);
}
