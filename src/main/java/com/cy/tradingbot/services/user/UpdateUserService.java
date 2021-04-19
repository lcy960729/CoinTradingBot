package com.cy.tradingbot.services.user;

import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class UpdateUserService {

    @Autowired
    private UserRepository userRepository;

    public void update(long userId, int maxOfCandle, int numOfMovingAverageWindow, String coinList){
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);

        user.setMaxOfCandles(maxOfCandle);
        user.setNumOfMovingAverageWindow(numOfMovingAverageWindow);
        user.setCoins(coinList.trim());

        user = userRepository.save(user);
    }
}
