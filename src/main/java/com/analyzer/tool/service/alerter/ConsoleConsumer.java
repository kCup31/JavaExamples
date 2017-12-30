package com.analyzer.tool.service.alerter;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * The console consumer
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class ConsoleConsumer extends AbstractConsumer implements Runnable {

    /**
     * The logger for this class
     */
    protected static final Logger LOG = Logger.getLogger(ConsoleConsumer.class);

    /**
     * Fills the blockingQueue for consoleConsumer with the string
     *
     * @param string
     */
    @Override
    void fillQueue(String string) {

        try {
            blockingQueue.put(string);
            LOG.info("Console Done filling queue : " + string + ", size: " + blockingQueue.size());
        }
        catch (InterruptedException e) {
            LOG.info(e.getMessage(), e);
        }
    }

    /**
     * Run implementation to read the blockingQueue
     */
    @Override
    public void run() {

        LOG.info("Console RUN");

        for(;;) {
            try {
                LOG.info("Console taking queue entry");
                String string = blockingQueue.take();
                LOG.info("Console took queue entry");
                System.out.println("CONSOLE OUT :: " + string);
            }
            catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
