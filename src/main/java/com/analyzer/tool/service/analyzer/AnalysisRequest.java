package com.analyzer.tool.service.analyzer;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * The analysis request queue used to fill the queue and read from the queue
 *
 * @author K Cup
 * @version 0.1
 */
@Builder
@Data
public class AnalysisRequest {

    /**
     * The File
     */
    private File filename;

    /**
     * The CountDownLatch
     */
    private CountDownLatch countDownLatch;

    /**
     * The ConcurrentLinkedQueue scanResultQueue
     */
    ConcurrentLinkedQueue<ScanResult> scanResultQueue;

}
