package com.cy.tradingbot.domain.entity;

import com.cy.tradingbot.dto.LogDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(name = "Log")
@Getter
@Setter
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDateTime dateTime;

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    @Column
    private String reason;

    public LogDTO toDTO() {
        LogDTO logDTO = new LogDTO();
        logDTO.setReason(reason);
        logDTO.setDateTime(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return logDTO;
    }
}
