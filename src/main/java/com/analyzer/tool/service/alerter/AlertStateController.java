package com.analyzer.tool.service.alerter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller acting based on the state passed by the user
 *
 * @author K Cup
 * @version 0.1
 */
@RestController
public class AlertStateController {

    /**
     * The broker
     */
    @Autowired
    Broker<String> broker;

    /**
     * Start the service
     */
    @GetMapping("/start")
    public void startState() {

        broker.changeState(ActiveState.START);
    }

    /**
     * Stop the service
     */
    @GetMapping("/stop")
    public void stopState() {

        broker.changeState(ActiveState.STOP);
    }
}
