package com.networkutilities.network_utilities_tool.cli;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArgumentParser {
    private String host;
    private int startPort = 1;
    private int endPort = 1024;
    private List<Integer> specificPorts = new ArrayList<>();
    private boolean quickScan = false;
    private boolean showClosedPorts = false;
    private boolean jsonOutput = false;
    private boolean verbose = false;
    private boolean help = false;
    private int threads = 50;
    private int timeout = 5000;

    public boolean parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            switch (arg) {
                case "-h", "--help":
                    help = true;
                    return true;
                case "-q", "--quick":
                    quickScan = true;
                    break;
                case "-v", "--verbose":
                    verbose = true;
                    break;
                case "-c", "--closed":
                    showClosedPorts = true;
                    break;
                case "-j", "--json":
                    jsonOutput = true;
                    break;
                case "-p", "--ports":
                    if (i + 1 < args.length) {
                        parsePortRange(args[++i]);
                    }
                    break;
                case "-sp", "--specific-ports":
                    if (i + 1 < args.length) {
                        parseSpecificPorts(args[++i]);
                    }
                    break;
                case "-t", "--threads":
                    if (i + 1 < args.length) {
                        try {
                            threads = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    break;
                case "--timeout":
                    if (i + 1 < args.length) {
                        try {
                            timeout = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    break;
                default:
                    if (!arg.startsWith("-") && host == null) {
                        host = arg;
                    }
                    break;
            }
        }
        
        return host != null || help;
    }

    private void parsePortRange(String portRange) {
        if (portRange.contains("-")) {
            String[] parts = portRange.split("-");
            if (parts.length == 2) {
                try {
                    startPort = Integer.parseInt(parts[0]);
                    endPort = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // Use defaults
                }
            }
        } else {
            try {
                int port = Integer.parseInt(portRange);
                startPort = port;
                endPort = port;
            } catch (NumberFormatException e) {
                // Use defaults
            }
        }
    }

    private void parseSpecificPorts(String ports) {
        String[] portArray = ports.split(",");
        specificPorts.clear();
        for (String port : portArray) {
            try {
                specificPorts.add(Integer.parseInt(port.trim()));
            } catch (NumberFormatException e) {
                // Skip invalid ports
            }
        }
    }

    public boolean isValid() {
        return host != null && !host.trim().isEmpty();
    }
}
