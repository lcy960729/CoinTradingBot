package com.cy.tradingbot;

import com.cy.tradingbot.component.ScheduleTasks.ProgrammableSchedulerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    @Autowired
    private ProgrammableSchedulerRunner programmableSchedulerRunner;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        programmableSchedulerRunner.runSchedule();
    }
}
