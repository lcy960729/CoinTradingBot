package com.cy.tradingbot.services.user;

import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.dto.UserDTO;
import com.cy.tradingbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserService {

    @Autowired
    private UserRepository userRepository;

    public User get(long userId){
        return userRepository.findById(userId).orElseThrow(RuntimeException::new);
    }

    public UserDTO getUserDTO(long userId){
        User user = get(userId);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setCoins(user.getCoins());
        userDTO.setMaxOfCandles(user.getMaxOfCandles());
        userDTO.setNumOfMovingAverageWindow(user.getNumOfMovingAverageWindow());

        return userDTO;
    }
}
