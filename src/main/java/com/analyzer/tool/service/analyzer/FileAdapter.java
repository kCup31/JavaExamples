package com.analyzer.tool.service.analyzer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Cleanup;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File adapter
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class FileAdapter {

    /**
     * The analysisAdapter instance
     */
    @Autowired
    AnalysisAdapter<String> analysisAdapter;

    /**
     * The maximum number of threads
     */
    private static final int MAX_THREADS = 10;

    /**
     * The maximum number of time for countdownLatch
     */
    private static final int MAX_LATCH_TIME_MILLIS = 30000;

    /**
     * The Logger for this class
     */
    private static final Logger LOG = LogManager.getLogger(FileAdapter.class);

    /**
     * Analyze File
     *
     * @param filePath filePath
     * @return returns list of scanResult
     */
    public List<ScanResult> analyzeFile(String filePath) {

        ConcurrentLinkedQueue<ScanResult> scanResultQueue = new ConcurrentLinkedQueue<>();

        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("RequestWorker-%d")
                .setDaemon(true)
                .build();

        @Cleanup("shutdownNow")
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS, threadFactory);

        try {

            LOG.info("Start analyzing files");

            // Get workload
            List<File> filePaths = Files.walk(Paths.get(filePath))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .collect(Collectors.toList());

            int numFiles = filePaths.size();

            CountDownLatch latch = new CountDownLatch(numFiles);

            // Build Queue
            BlockingQueue<AnalysisRequest> queue = buildQueue(filePaths, latch, scanResultQueue);

            // StartConsumerThread to read from the queue
            startConsumerThread(queue, executor);

            long startTime = System.currentTimeMillis();
            LOG.info("CountDownLatch starts to wait ::" + startTime + " ms");

            // Wait for all the threads to finish
            latch.await(MAX_LATCH_TIME_MILLIS, TimeUnit.MILLISECONDS);

            LOG.info("End of analyzing files ::" + (System.currentTimeMillis() - startTime) + " ms");

        }
        catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        return scanResultQueue.stream().collect(Collectors.toList());
    }

    /**
     * produces the blockingQueue
     *
     * @param filePaths       List of files
     * @param latch           The countdownlatch
     * @param scanResultQueue ConcurrentLinkedQueue containing ScanResult
     * @return BlockingQueue containing AnalysisRequest
     */
    private BlockingQueue<AnalysisRequest> buildQueue(List<File> filePaths, CountDownLatch latch,
            ConcurrentLinkedQueue<ScanResult> scanResultQueue) {

        final BlockingQueue<AnalysisRequest> queue = new ArrayBlockingQueue<>(filePaths.size());

        // Fill the queue
        filePaths.stream().forEach(file -> {
            try {
                queue.put(AnalysisRequest.builder()
                        .countDownLatch(latch)
                        .filename(file)
                        .scanResultQueue(scanResultQueue).build());
            }

            catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        });

        LOG.info("Built the queue");

        return queue;
    }

    /**
     * Starts the consumer thread
     *
     * @param queue    The ArrayBlockingQueue
     * @param executor The executor service
     */
    private void startConsumerThread(BlockingQueue<AnalysisRequest> queue, ExecutorService executor) {

        LOG.info("Start Consumer Thread");

        List<ConsumerWorker> consumerWorkerList = Stream.generate(() -> new ConsumerWorker(queue, analysisAdapter))
                .limit(MAX_THREADS)
                .collect(Collectors.toList());

        consumerWorkerList.forEach(executor::submit);

        LOG.info("Main Thread will exit the method now while ConsumerWorker Thread is running");
    }
}
