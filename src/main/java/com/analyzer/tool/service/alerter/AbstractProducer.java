package com.analyzer.tool.service.alerter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The base class for the producers
 *
 * @author K Cup
 * @version 0.1
 */
public abstract class AbstractProducer {

    /**
     * The executorservice responsible for controlling threads
     */
    protected ExecutorService executorService = null;

    /**
     * The broker
     */
    protected Broker<String> broker;

    /**
     * The current active state
     */
    protected ActiveState currentState = ActiveState.STOP;

    /**
     * The max wait time for the thread
     */
    protected static final long WAIT_TIME = 500L;

    /**
     * The logger for this class
     */
    private static final Logger LOG = Logger.getLogger(AbstractProducer.class);

    /**
     * Register producers on the startup
     *
     * @param broker The broker
     */
    @Autowired
    private void registerProducer(Broker<String> broker) {

        this.broker = broker;
        broker.registerProducers(this::changeState);
    }

    /**
     * Change state based on the state received
     *
     * @param state ActiveState
     */
    private void changeState(ActiveState state) {

        if (state.equals(ActiveState.START)) {

            startService();
        }
        else if (state.equals(ActiveState.STOP)) {

            stopService();
        }
    }

    /**
     * Start service
     */
    private void startService() {

        LOG.info("Start running producer Threads : " + Thread.currentThread().getName());

        this.currentState = ActiveState.START;

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Producer-%d")
                .setDaemon(true)
                .build();

        this.executorService = Executors.newSingleThreadExecutor(threadFactory);

        executorService.submit(this::run);

        LOG.info("Exit Method startrService from AbstractProducer ");
    }

    /**
     * Stop service
     */
    private void stopService() {

        LOG.info("Stop Running");

        this.currentState = ActiveState.STOP;

        if (executorService != null) {
            executorService.shutdownNow();
        }

        LOG.info("Stopped Running");
    }

    abstract void run();

}
