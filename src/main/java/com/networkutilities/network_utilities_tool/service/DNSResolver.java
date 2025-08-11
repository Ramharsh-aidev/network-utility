package com.networkutilities.network_utilities_tool.service;

import com.networkutilities.network_utilities_tool.config.ScanConfiguration;
import com.networkutilities.network_utilities_tool.util.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class DNSResolver {
    
    private final ScanConfiguration config;
    private final Logger logger;
    private final ConcurrentMap<String, String> dnsCache = new ConcurrentHashMap<>();

    public String resolveHostname(String host) {
        if (!config.isEnableDnsResolution()) {
            return host;
        }

        if (dnsCache.containsKey(host)) {
            return dnsCache.get(host);
        }

        try {
            InetAddress address = InetAddress.getByName(host);
            String hostname = address.getHostName();
            
            dnsCache.put(host, hostname);
            
            if (!hostname.equals(host)) {
                logger.debug("Resolved {} to {}", host, hostname);
                return hostname;
            }
        } catch (UnknownHostException e) {
            logger.debug("Could not resolve hostname for {}: {}", host, e.getMessage());
        }
        
        return host;
    }

    public String resolveIPAddress(String hostname) {
        if (!config.isEnableDnsResolution()) {
            return hostname;
        }

        String cacheKey = "ip_" + hostname;
        if (dnsCache.containsKey(cacheKey)) {
            return dnsCache.get(cacheKey);
        }

        try {
            InetAddress address = InetAddress.getByName(hostname);
            String ipAddress = address.getHostAddress();
            
            dnsCache.put(cacheKey, ipAddress);
            
            logger.debug("Resolved {} to IP {}", hostname, ipAddress);
            return ipAddress;
        } catch (UnknownHostException e) {
            logger.debug("Could not resolve IP for {}: {}", hostname, e.getMessage());
            return hostname;
        }
    }

    public boolean isReachable(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.isReachable(config.getConnectTimeout());
        } catch (Exception e) {
            logger.debug("Host {} is not reachable: {}", host, e.getMessage());
            return false;
        }
    }

    public void clearCache() {
        dnsCache.clear();
        logger.info("DNS cache cleared");
    }
}
