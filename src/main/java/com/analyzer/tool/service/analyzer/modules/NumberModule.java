package com.analyzer.tool.service.analyzer.modules;

import com.analyzer.tool.service.analyzer.ScanResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Analyzes the string to see if it contains any number between 0-9 and generates a list of ScanResults.
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class NumberModule extends AbstractBaseModule {

    /**
     * The score for the number module
     */
    private static final int SCORE = 100;

    /**
     * The logger for this class
     */
    private static final Logger LOG = Logger.getLogger(NumberModule.class);

    /**
     * Analyze string and generate list based on the number it contains
     *
     * @param string string
     * @return List of ScanResult
     */
    @Override
    public List<ScanResult> analyze(String string) {

        LOG.info("Start analyzing number module [" + string + "]");

        List<ScanResult> scanResults =  IntStream.range(0,9)
                .mapToObj(num -> Integer.toString(num))
                .filter(num -> string.contains(num))
                .map(num -> ScanResult.builder()
                        .moduleName(this.getClass().getName())
                        .reason("Number found : " + num)
                        .score(ThreadLocalRandom.current().nextInt(SCORE + 1))
                        .threadName(Thread.currentThread().getName()).build())
                .collect(Collectors.toList());


        LOG.info("Done analyzing number module [" + string + "]");

        return scanResults;
    }
}
