package com.cy.tradingbot.controller;

import com.cy.tradingbot.component.ScheduleTasks.ProgrammableSchedulerRunner;
import com.cy.tradingbot.component.TimeCalculator;
import com.cy.tradingbot.configure.CustomUserDetail;
import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.dto.UserDTO;
import com.cy.tradingbot.services.CreateTradingBotService;
import com.cy.tradingbot.services.GetTradingBotService;
import com.cy.tradingbot.services.log.LogService;
import com.cy.tradingbot.services.log.RecordService;
import com.cy.tradingbot.services.user.GetUserService;
import com.cy.tradingbot.services.user.UpdateUserService;
import com.cy.tradingbot.services.DeleteTradingBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private RecordService recordService;
    @Autowired
    private LogService logService;
    @Autowired
    private GetTradingBotService getTradingBotService;
    @Autowired
    private CreateTradingBotService createTradingBotService;
    @Autowired
    private DeleteTradingBotService deleteTradingBotService;

    @Autowired
    private UpdateUserService updateUserService;
    @Autowired
    private GetUserService getUserService;

    @GetMapping("/")
    public ModelAndView home(@AuthenticationPrincipal CustomUserDetail userDetail) {
        UserDTO userDTO = getUserService.getUserDTO(userDetail.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");

        modelAndView.addObject("maxOfCandle", userDTO.getMaxOfCandles());
        modelAndView.addObject("numOfMovingAverageWindow", userDTO.getNumOfMovingAverageWindow());
        modelAndView.addObject("coinList", userDTO.getCoins());

        modelAndView.addObject("records", recordService.getAll(userDetail.getUserId()));
        modelAndView.addObject("logs", logService.getAll(userDetail.getUserId()));

        if (getTradingBotService.isNotExist(userDTO.getUserName())) {
            modelAndView.addObject("state", "ready");
            return modelAndView;
        }

        modelAndView.addObject("state", "running");

        modelAndView.addObject("closingTime", getTradingBotService.getClosingTIme(userDTO.getUserName()));
        modelAndView.addObject("currentTime", TimeCalculator.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        modelAndView.addObject("coins", getTradingBotService.getCoinInfoList(userDTO.getUserName()));

        return modelAndView;
    }

    @PostMapping("/start")
    public RedirectView start(@AuthenticationPrincipal CustomUserDetail userDetail, HttpServletRequest httpServletRequest) {
        User user = getUserService.get(userDetail.getUserId());

        final String userName = user.getUserName();

        if (getTradingBotService.isExist(userName)) {
            deleteTradingBotService.delete(userName);
        } else {
            Map<String, String[]> params = httpServletRequest.getParameterMap();
            updateUserService.update(userDetail.getUserId(),
                    Integer.parseInt(params.get("maxOfCandle")[0]),
                    Integer.parseInt(params.get("numOfMovingAverageWindow")[0]),
                    params.get("coinList")[0]);

            createTradingBotService.create(user);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new RedirectView("/");
    }
}
