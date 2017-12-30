package com.analyzer.tool.service.alerter;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * The Udp Producer class
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class UdpProducer extends AbstractProducer implements Runnable {

    /**
     * The Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(UdpProducer.class);

    /**
     * Continue to run until the current state changes to stop
     */
    @Override
    public void run() {

        LOG.info("Starting UDP Producer : " + Thread.currentThread().getName());

        int counter = 0;
        while (!currentState.equals(ActiveState.STOP)) {

            try {
                // Call the method on Broker and pass a new tcp string
                String udpString = ("UDP-0" + counter++);
                Thread.sleep(WAIT_TIME);

                broker.alertConsumers(udpString);
            }
            catch (InterruptedException e) {

                if (!currentState.equals(ActiveState.STOP)) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        LOG.info("Main thread out of UdpProducer Alert function");
    }
}
