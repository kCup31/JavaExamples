package com.analyzer.tool.service.analyzer;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * The consumer worker which reads from the queue and sends to the adapter to process each file
 *
 * @author K Cup
 * @version 0.1
 */
public class ConsumerWorker implements Runnable {

    /**
     * The blockingQueue containing AnalysisRequest
     */
    private BlockingQueue<AnalysisRequest> queue;

    /**
     * The analysisAdapter
     */
    private AnalysisAdapter<String> analysisAdapter;

    /**
     * The logger for this class
     */
    private static final Logger LOG = Logger.getLogger(ConsumerWorker.class);

    /**
     * The constructor
     *
     * @param queue           The queue
     * @param analysisAdapter The analysisAdapter
     */
    public ConsumerWorker(BlockingQueue<AnalysisRequest> queue, AnalysisAdapter<String> analysisAdapter) {
        this.queue = queue;
        this.analysisAdapter = analysisAdapter;
    }

    /**
     * The run method implementation for Runnable
     */
    @Override
    public void run() {

        AnalysisRequest analysisRequest;

        LOG.info("Start Thread in ConsumerWorker " + Thread.currentThread().getName());

        while ((analysisRequest = queue.poll()) != null) {

            // Read each bytes from the file
            Path path = Paths.get(analysisRequest.getFilename().getAbsolutePath());

            Optional<String> fileData = getFileContent(path);

            if (fileData.isPresent()) {

                List<ScanResult> scanResultList = analysisAdapter.analyze(fileData.get());

                analysisRequest.getScanResultQueue().addAll(scanResultList);
            }

            analysisRequest.getCountDownLatch().countDown();
        }

        LOG.info("End of Thread in ConsumerWorker " + Thread.currentThread().getName());
    }

    /**
     * The Path containing the file
     *
     * @param path The path
     * @return returns an optional file contents of type string
     */
    private Optional<String> getFileContent(Path path) {

        Optional<String> fileData = Optional.empty();

        try {

            fileData = Optional.of(new String(Files.readAllBytes(path)));
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info("End of file content execution in thread : " + Thread.currentThread().getName());
        return fileData;
    }
}

