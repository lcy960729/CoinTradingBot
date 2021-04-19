package com.cy.tradingbot.services.order;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestOrderService {
    @Autowired
    private GetOrderService getOrderService;

    @Autowired
    private DeleteOrderService deleteOrderService;

    @Autowired
    private CreateOrderService createOrderService;

    public void requestOrder(OrderSheet orderSheet) {
        Order order = createOrder(orderSheet);

        while (isNotCompletedOrder(order.getUuid())) {
            deleteOrder(order);

            order = createOrder(orderSheet);
        }
    }

    private void deleteOrder(Order order) {
        deleteOrderService.deleteOrder(order.getUuid());
    }

    private boolean isNotCompletedOrder(String uuid) {
        return getOrderService.getOrder(uuid).isWait();
    }

    private Order createOrder(OrderSheet orderSheet) {
        return createOrderService.create(orderSheet);
    }
}
