package com.cy.tradingbot.services.order;

import com.cy.tradingbot.domain.Order;
import com.cy.tradingbot.domain.OrderSheet;

public interface CreateOrderService {
    Order create(OrderSheet orderSheet);
}
