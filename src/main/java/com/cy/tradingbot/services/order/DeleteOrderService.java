package com.cy.tradingbot.services.order;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface DeleteOrderService {
    void deleteOrder(String uuid);
}
