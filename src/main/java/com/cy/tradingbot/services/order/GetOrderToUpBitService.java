package com.cy.tradingbot.services.order;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetOrderToUpBitService implements GetOrderService{
    @Autowired
    private UpBitAPI upbitAPI;

    public Order getOrder(String uuid) {
        return upbitAPI.getOrder(uuid).orElseThrow(RuntimeException::new);
    }
}
