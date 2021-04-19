package com.cy.tradingbot.services.candle;


import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCandlesToUpBitService implements GetCandlesService {
    @Autowired
    private UpBitAPI upbitAPI;

    public List<Candle> getCandles(String coinName, int maxOfCandles) {
        return upbitAPI.getDayCandle(coinName, maxOfCandles).orElseThrow(RuntimeException::new);
    }
}
