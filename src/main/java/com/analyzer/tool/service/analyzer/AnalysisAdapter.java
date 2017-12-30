package com.analyzer.tool.service.analyzer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The analysis adapter
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class AnalysisAdapter<T> {

    /**
     * The Max Timer for Thread to wait
     */
    private static final int MAX_TIMER = 25;

    /**
     * The Threshold for score to pass
     */
    private static final int THRESHOLD = 5;

    /**
     * List of scanResult providers
     */
    private List<Function<T, List<ScanResult>>> scanResultProviders = new ArrayList<>();

    /**
     * The logger for the class
     */
    private static final Logger LOG = Logger.getLogger(AnalysisAdapter.class);

    /**
     * The analyze controller
     *
     * @param obj Object
     * @return List of ScanResult
     */
    public List<ScanResult> analyze(T obj) {

        final List<ScanResult> lists = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(scanResultProviders.size());

        try {
            LOG.info("Start analyzing ");

            final List<Future<List<ScanResult>>> futures = new ArrayList<>();

            scanResultProviders.stream().forEach(function -> {
                Callable<List<ScanResult>> callable = () -> function.apply(obj);
                futures.add(executor.submit(callable));
            });

            futures.stream().forEach(future -> {
                try {
                    lists.addAll(future.get(MAX_TIMER, TimeUnit.SECONDS));
                }
                catch (ExecutionException | TimeoutException | InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            });

            LOG.info("Done analyzing [" + obj + "]");

        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        finally {
            executor.shutdownNow();
        }

        return lists.stream()
                .filter(i -> i.getScore() > THRESHOLD)
                .sorted(Comparator.comparing(ScanResult::getScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Register the module and adds it to the list
     *
     * @param function
     */
    public void registerModuleHandler(Function<T, List<ScanResult>> function) {

        scanResultProviders.add(function);
    }
}
