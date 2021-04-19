package com.cy.tradingbot.services.order;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderToUpBitService implements DeleteOrderService{
    @Autowired
    private UpBitAPI upbitAPI;

    public void deleteOrder(String uuid){
        upbitAPI.deleteOrder(uuid);
    }
}
