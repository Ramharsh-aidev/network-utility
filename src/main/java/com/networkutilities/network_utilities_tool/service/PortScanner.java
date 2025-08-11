package com.networkutilities.network_utilities_tool.service;

import com.networkutilities.network_utilities_tool.config.ScanConfiguration;
import com.networkutilities.network_utilities_tool.model.ScanResult;
import com.networkutilities.network_utilities_tool.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class PortScanner {
    
    private final ScanConfiguration config;
    private final ServiceDetector serviceDetector;
    private final Logger logger;

    public List<ScanResult> scanPortRange(String host, int startPort, int endPort) {
        logger.info("Starting port scan for host: {} on ports {}-{}", host, startPort, endPort);
        
        List<ScanResult> results = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(config.getMaxThreads());
        List<Future<ScanResult>> futures = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            Future<ScanResult> future = executor.submit(() -> scanSinglePort(host, currentPort));
            futures.add(future);
        }

        for (Future<ScanResult> future : futures) {
            try {
                ScanResult result = future.get(config.getDefaultTimeout(), TimeUnit.MILLISECONDS);
                results.add(result);
                if (result.isOpen()) {
                    logger.info("Open port found: {}:{}", host, result.getPort());
                }
            } catch (TimeoutException e) {
                logger.warn("Timeout scanning port on {}", host);
                results.add(ScanResult.errorResult(host, -1, "Scan timeout"));
            } catch (Exception e) {
                logger.error("Error during port scan: {}", e.getMessage());
                results.add(ScanResult.errorResult(host, -1, e.getMessage()));
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        logger.info("Port scan completed for host: {}", host);
        return results;
    }

    public ScanResult scanSinglePort(String host, int port) {
        long startTime = System.currentTimeMillis();
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), config.getConnectTimeout());
            long responseTime = System.currentTimeMillis() - startTime;
            
            String service = serviceDetector.detectService(port, host);
            return ScanResult.openPort(host, port, service, responseTime);
            
        } catch (IOException e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return ScanResult.closedPort(host, port, responseTime);
        }
    }

    public List<ScanResult> scanSpecificPorts(String host, List<Integer> ports) {
        logger.info("Starting specific port scan for host: {} on {} ports", host, ports.size());
        
        List<ScanResult> results = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(config.getMaxThreads());
        List<Future<ScanResult>> futures = new ArrayList<>();

        for (Integer port : ports) {
            Future<ScanResult> future = executor.submit(() -> scanSinglePort(host, port));
            futures.add(future);
        }

        for (Future<ScanResult> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                logger.error("Error during specific port scan: {}", e.getMessage());
            }
        }

        executor.shutdown();
        return results;
    }
}
