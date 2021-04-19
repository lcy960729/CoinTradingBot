package com.cy.tradingbot.services.candle;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface GetTickerService {
    Candle getTicker(String coinName);
}
