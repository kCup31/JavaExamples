package com.analyzer.tool.service.alerter;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * The syslog consumer
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class SyslogConsumer extends AbstractConsumer implements Runnable {

    /**
     * The logger for this class
     */
    protected static final Logger LOG = Logger.getLogger(SyslogConsumer.class);

    /**
     * This is used to fill the blockingQueue with the string
     *
     * @param string string
     */
    @Override
    void fillQueue(String string) {

        try {
            blockingQueue.put(string);

            LOG.info("Syslog Done filling queue : " + string + ", size: " + blockingQueue.size());
        }
        catch (InterruptedException e) {
            LOG.info(e.getMessage(), e);
        }
    }

    /**
     * The run implementation to read from the blockingQueue
     */
    @Override
    public void run() {

        LOG.info("Syslog RUN");

        for (;;) {

            try {
                String string = blockingQueue.take();
                System.out.println("SYSLOG OUT :: " + string);
            }
            catch (InterruptedException e) {
                LOG.info(e.getMessage(), e);
            }
        }
    }
}
