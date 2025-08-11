package com.networkutilities.network_utilities_tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "network.scan")
public class ScanConfiguration {
    private int defaultTimeout = 5000;
    private int maxThreads = 100;
    private int defaultPortRangeStart = 1;
    private int defaultPortRangeEnd = 1024;
    private boolean enableServiceDetection = true;
    private boolean enableDnsResolution = true;
    private int connectTimeout = 3000;
    private int readTimeout = 2000;
}
