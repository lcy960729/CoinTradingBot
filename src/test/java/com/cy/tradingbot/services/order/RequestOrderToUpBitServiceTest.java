package com.cy.tradingbot.services.order;

import com.cy.tradingbot.domain.Order;
import com.cy.tradingbot.domain.OrderSheet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RequestOrderToUpBitServiceTest {
//
//    @Autowired
//    private RequestOrderService requestOrderService;
//
//    @Autowired
//    private GetOrderService getOrderService;
//
//    @Test
//    void requestBuyingOrder() {
//        OrderSheet orderSheet = OrderSheet.builder()
//                .coinName("KRW-BTT")
//                .side("bid")
//                .price(10000.0)
//                .orderType("price")
//                .build();
//
//        Order orderResult = requestOrderService.requestOrder(orderSheet);
//
//        Order order = getOrderService.getOrder(orderResult.getUuid());
//
//        assertThat(orderResult.getState()).isEqualTo("done");
//    }
//
//    @Test
//    void requestSellOrder() {
//        OrderSheet orderSheet = OrderSheet.builder()
//                .coinName("KRW-BTT")
//                .side("ask")
//                .volume(877.19298245)
//                .orderType("market")
//                .build();
//
//        Order orderResult = requestOrderService.requestOrder(orderSheet);
//
//        assertThat(orderResult.getState()).isEqualTo("done");
//    }
}