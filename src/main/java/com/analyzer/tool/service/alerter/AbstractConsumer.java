package com.analyzer.tool.service.alerter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The abstract consumer
 *
 * @author K Cup
 * @version 0.1
 */
public abstract class AbstractConsumer {

    /**
     * The blockingQueue containing string
     */
    protected BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(20);

    /**
     * The executorService
     */
    protected ExecutorService executorService;

    /**
     * The logger for this class
     */
    protected Logger LOG = Logger.getLogger(AbstractConsumer.class);

    /**
     * The constructor
     */
    public AbstractConsumer() {

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("consumerT-%d")
                .setDaemon(true)
                .build();
        executorService = Executors.newSingleThreadExecutor(threadFactory);

        startThread();
    }

    /**
     * Registers the consumers to fillQueue to broker
     *
     * @param broker
     */
    @Autowired
    private void registerConsumerFillQueue(Broker<String> broker) {

        broker.registerConsumersFillQueue(this::fillQueue);
    }

    /**
     * Abstract fillQueue
     * @param string string
     */
    abstract void fillQueue(String string);

    /**
     * Abstract run
     */
    abstract void run();

    /**
     * Start Thread
     */
    private void startThread() {

        LOG.info("Starting Thread");

        executorService.submit(this::run);

        LOG.info("Started Thread");
    }
}
