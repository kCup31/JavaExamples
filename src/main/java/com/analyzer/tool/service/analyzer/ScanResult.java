package com.analyzer.tool.service.analyzer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * The scan result
 *
 * @author K Cup
 * @version 0.1
 */
@AllArgsConstructor
@Builder
@Data
public class ScanResult {

    private Integer score;

    private String reason;

    private String moduleName;

    private String threadName;
}
