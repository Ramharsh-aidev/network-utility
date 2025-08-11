package com.networkutilities.network_utilities_tool.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ScanSummary {
    private String targetHost;
    private int totalPortsScanned;
    private int openPorts;
    private int closedPorts;
    private int errorPorts;
    private long totalScanTime;
    private LocalDateTime scanStartTime;
    private LocalDateTime scanEndTime;
    private List<ScanResult> results;
}
