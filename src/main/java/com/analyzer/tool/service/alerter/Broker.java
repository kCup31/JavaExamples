package com.analyzer.tool.service.alerter;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The broker class
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class Broker<T> {

    /**
     * The Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(Broker.class);

    /**
     * The list  consumer interface which is responsible to fill the blockingQueue to their respective consumer.
     */
    private List<Consumer<T>> consumersFillQueue = new ArrayList<>();

    /**
     * The list of consumer interface for producer registered to run
     */
    private List<Consumer<ActiveState>> producerProviders = new ArrayList<>();

    /**
     * Alert consumers with string responsible for running each of the consumer interface from the list to fill
     * their respective Queue.
     *
     * @param alert alert
     */
    public void alertConsumers(T alert) {

        LOG.info("Start of Broker");

        // Fill Queue
        consumersFillQueue.stream().forEach(i -> i.accept(alert));

        LOG.info("End of broker");
    }

    /**
     * Changes the state for each of the producers running from active start to active stop or vice-versa.
     *
     * @param state activestate
     */
    public void changeState(ActiveState state) {

        producerProviders.stream().forEach(i -> i.accept(state));
    }

    /**
     * Creates a list of consumers to fillQueue
     *
     * @param consumer Consumer interface
     */
    public void registerConsumersFillQueue(Consumer<T> consumer) {

        consumersFillQueue.add(consumer);
    }

    /**
     * Creates a list of consumer interface of providers
     *
     * @param consumer consumer interface
     */
    public void registerProducers(Consumer<ActiveState> consumer) {

        producerProviders.add(consumer);
    }
}
