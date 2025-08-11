package com.networkutilities.network_utilities_tool.service;

import com.networkutilities.network_utilities_tool.model.ScanResult;
import com.networkutilities.network_utilities_tool.model.ScanSummary;
import com.networkutilities.network_utilities_tool.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NetworkScanner {
    
    private final PortScanner portScanner;
    private final DNSResolver dnsResolver;
    private final Logger logger;

    public ScanSummary performFullScan(String host, int startPort, int endPort) {
        logger.info("Starting full network scan for host: {}", host);
        LocalDateTime scanStart = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        String resolvedHostname = dnsResolver.resolveHostname(host);
        List<ScanResult> results = portScanner.scanPortRange(host, startPort, endPort);
        
        results.forEach(result -> result.setResolvedHostname(resolvedHostname));
        
        LocalDateTime scanEnd = LocalDateTime.now();
        long totalTime = System.currentTimeMillis() - startTime;

        int openPorts = (int) results.stream().filter(ScanResult::isOpen).count();
        int closedPorts = (int) results.stream().filter(r -> !r.isOpen() && r.getErrorMessage() == null).count();
        int errorPorts = (int) results.stream().filter(r -> r.getErrorMessage() != null).count();

        ScanSummary summary = ScanSummary.builder()
                .targetHost(host)
                .totalPortsScanned(results.size())
                .openPorts(openPorts)
                .closedPorts(closedPorts)
                .errorPorts(errorPorts)
                .totalScanTime(totalTime)
                .scanStartTime(scanStart)
                .scanEndTime(scanEnd)
                .results(results)
                .build();

        logger.info("Full scan completed for host: {} in {}ms", host, totalTime);
        return summary;
    }

    public ScanSummary performQuickScan(String host) {
        List<Integer> commonPorts = List.of(
            21, 22, 23, 25, 53, 80, 110, 443, 993, 995, 
            1723, 3389, 5900, 8080, 8443
        );
        
        logger.info("Starting quick scan for host: {}", host);
        LocalDateTime scanStart = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        String resolvedHostname = dnsResolver.resolveHostname(host);
        List<ScanResult> results = portScanner.scanSpecificPorts(host, commonPorts);
        
        results.forEach(result -> result.setResolvedHostname(resolvedHostname));
        
        LocalDateTime scanEnd = LocalDateTime.now();
        long totalTime = System.currentTimeMillis() - startTime;

        int openPorts = (int) results.stream().filter(ScanResult::isOpen).count();
        int closedPorts = (int) results.stream().filter(r -> !r.isOpen() && r.getErrorMessage() == null).count();
        int errorPorts = (int) results.stream().filter(r -> r.getErrorMessage() != null).count();

        return ScanSummary.builder()
                .targetHost(host)
                .totalPortsScanned(results.size())
                .openPorts(openPorts)
                .closedPorts(closedPorts)
                .errorPorts(errorPorts)
                .totalScanTime(totalTime)
                .scanStartTime(scanStart)
                .scanEndTime(scanEnd)
                .results(results)
                .build();
    }
}
