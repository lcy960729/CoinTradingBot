package com.cy.tradingbot.component.ScheduleTasks;

import com.cy.tradingbot.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgrammableSchedulerRunner {
    @Autowired
    private ProgrammableScheduler scheduler;

    public void runSchedule() {
        scheduler.startScheduler();
    }

    public void stopSchedule() {
        scheduler.stopScheduler();
    }
}
