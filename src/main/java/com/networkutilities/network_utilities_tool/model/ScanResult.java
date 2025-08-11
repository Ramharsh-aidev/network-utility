package com.networkutilities.network_utilities_tool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {
    private String targetHost;
    private String resolvedHostname;
    private int port;
    private boolean isOpen;
    private String service;
    private String banner;
    private long responseTime;
    private LocalDateTime scanTime;
    private String errorMessage;
    
    public static ScanResult openPort(String host, int port, String service, long responseTime) {
        return ScanResult.builder()
                .targetHost(host)
                .port(port)
                .isOpen(true)
                .service(service)
                .responseTime(responseTime)
                .scanTime(LocalDateTime.now())
                .build();
    }
    
    public static ScanResult closedPort(String host, int port, long responseTime) {
        return ScanResult.builder()
                .targetHost(host)
                .port(port)
                .isOpen(false)
                .responseTime(responseTime)
                .scanTime(LocalDateTime.now())
                .build();
    }
    
    public static ScanResult errorResult(String host, int port, String error) {
        return ScanResult.builder()
                .targetHost(host)
                .port(port)
                .isOpen(false)
                .errorMessage(error)
                .scanTime(LocalDateTime.now())
                .build();
    }
}
