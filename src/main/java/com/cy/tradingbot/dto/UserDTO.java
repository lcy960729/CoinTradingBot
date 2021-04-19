package com.cy.tradingbot.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO {
    private String userName;
    private Integer maxOfCandles;
    private Integer numOfMovingAverageWindow;
    private String coins;
}
