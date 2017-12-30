package com.analyzer.tool.service.analyzer.modules;

import com.analyzer.tool.service.analyzer.ScanResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analyzes the string to see if it contains any vowel (case insensitive) and generates a list of ScanResult.
 *
 * @author K Cup
 * @version 0.1
 */
@Service
public class VowelModule extends AbstractBaseModule {

    /**
     * Max score for this module
     */
    private static final Integer MAX_SCORE = 50;

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(VowelModule.class);

    /**
     * Analyze vowel
     *
     * @param string string
     * @return List of ScanResult
     */
    @Override
    public List<ScanResult> analyze(String string) {

        LOG.info("Started analyzing vowel [" + string + "]");

        List<ScanResult> scanResults = Stream.of("a", "e", "i", "o", "u", "A", "E", "I", "O", "U")
                .filter(vowel -> string.contains(vowel))
                .map(vowel -> ScanResult.builder()
                        .threadName(Thread.currentThread().getName())
                        .score(ThreadLocalRandom.current().nextInt(MAX_SCORE + 1))
                        .reason("String contains : " + vowel)
                        .moduleName(this.getClass().getName()).build())
                .collect(Collectors.toList());

        LOG.info("Done analyzing vowel [" + string + "]");

        return scanResults;
    }
}
