package com.cy.tradingbot.component.ScheduleTasks;

import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.services.CreateTradingBotService;
import com.cy.tradingbot.services.DeleteTradingBotService;
import com.cy.tradingbot.services.GetTradingBotService;
import com.cy.tradingbot.services.TradeTradingBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProgrammableScheduler {
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private TradeTradingBotService tradeTradingBotService;
    @Autowired
    private GetTradingBotService getTradingBotService;
    @Autowired
    private DeleteTradingBotService deleteTradingBotService;

    public void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void startScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        scheduler.schedule(getRunnable(), getTrigger());
    }

    private Runnable getRunnable() {
        return () -> {
            List<TradingBot> tradingBotList = getTradingBotService.getTradingBots();

            deleteTradingBotService.emptyAllGarbage();

            tradingBotList.forEach(tradingBot -> {
                try {
                    tradeTradingBotService.trade(tradingBot);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            deleteTradingBotService.emptyAllGarbage();
        };
    }

    public Trigger getTrigger() {
        return new PeriodicTrigger(100, TimeUnit.MILLISECONDS);
    }
}
