package com.analyzer.tool.service.alerter;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * The Tcp producer class
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class TcpProducer extends AbstractProducer implements Runnable{

    /**
     * The Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(TcpProducer.class);

    /**
     * The run implementation to send Tcp alert until the currentstate changes to stop
     */
    @Override
    public void run() {

        LOG.info("Starting TCP Producer : " + Thread.currentThread().getName());

        int counter = 0;

        while(!currentState.equals(ActiveState.STOP)) {
            try {
                // Call the method on Broker and pass a new tcp string
                String tcpString = ("TCP-0" + counter++);

                Thread.sleep(WAIT_TIME);
                broker.alertConsumers(tcpString);
            }
            catch (InterruptedException e) {

                if (!currentState.equals(ActiveState.STOP)) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        LOG.info("Main thread out of TCP Producer Alert Function");
    }
}