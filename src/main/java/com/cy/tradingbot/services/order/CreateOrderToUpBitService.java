package com.cy.tradingbot.services.order;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Order;
import com.cy.tradingbot.domain.OrderSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderToUpBitService implements CreateOrderService{
    @Autowired
    private UpBitAPI upBitAPI;

    @Override
    public Order create(OrderSheet orderSheet) {
        return upBitAPI.order(
                orderSheet.getCoinName(),
                orderSheet.getSide(),
                orderSheet.getVolume(),
                orderSheet.getPrice(),
                orderSheet.getOrderType()
        ).orElseThrow(RuntimeException::new);
    }
}
