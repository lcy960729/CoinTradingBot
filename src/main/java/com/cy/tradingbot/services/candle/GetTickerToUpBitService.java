package com.cy.tradingbot.services.candle;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetTickerToUpBitService implements GetTickerService{
    @Autowired
    private UpBitAPI upbitAPI;

    public Candle getTicker(String coinName){
        return upbitAPI.getTicker(coinName).orElseThrow(RuntimeException::new).get(0);
    }
}
