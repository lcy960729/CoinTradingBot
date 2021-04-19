package com.cy.tradingbot.domain.entity;

import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.domain.entity.Log;
import com.cy.tradingbot.domain.entity.Record;
import com.cy.tradingbot.dto.LogDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "User")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String userName;

    @Column
    private String password;

    @Column(name = "accessKey")
    private String accessKey;

    @Column(name = "secretKey")
    private String secretKey;

    @Column(name = "maxOfCandles")
    private Integer maxOfCandles;

    @Column(name = "numOfMovingAverageWindow")
    private Integer numOfMovingAverageWindow;

    @Column(name = "coins")
    private String coins;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Record> recordList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Log> logList;

    public List<String> getCoinList() {
        return Arrays.stream(coins.trim().split(" ")).collect(Collectors.toList());
    }
}