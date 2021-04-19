package com.cy.tradingbot.domain.entity;

import com.cy.tradingbot.dto.RecordDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(name = "Record")
@Getter
@Setter
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDateTime dateTime;

    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    @Column
    private Double balance;

    public RecordDTO toDTO() {
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setBalance(String.format("%,d", balance.intValue()));
        recordDTO.setDateTime(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return recordDTO;
    }
}
