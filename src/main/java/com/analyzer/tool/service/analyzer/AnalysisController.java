package com.analyzer.tool.service.analyzer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The controller
 *
 * @author K Cup
 * @version 0.1
 */
@RestController
public class AnalysisController {

    @Autowired
    AnalysisAdapter<String> adapter;

    @Autowired
    FileAdapter fileAdapter;

    /**
     * The logger for the class
     */
    private static final Logger LOG = Logger.getLogger(AnalysisController.class);

    /**
     * The analyze controller
     * @param string string
     * @return List of ScanResult
     */
    @GetMapping("/analyze")
    public List<ScanResult> analyze(@RequestParam String string) {

        return adapter.analyze(string);
    }

    /**
     * The analyze controller
     * @param filepath string
     * @return List of ScanResult
     */
        @GetMapping("/analyzeFiles")
    public List<ScanResult> analyzeFile(@RequestParam String filepath) {

        return fileAdapter.analyzeFile(filepath);
    }
}