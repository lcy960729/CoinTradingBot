package com.cy.tradingbot.services.candle;


import com.cy.tradingbot.domain.Candle;

import java.util.List;

public interface GetCandlesService {
    List<Candle> getCandles(String coinName, int maxOfCandles);
}
