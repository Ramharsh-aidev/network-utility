package com.networkutilities.network_utilities_tool;

import com.networkutilities.network_utilities_tool.cli.CommandLineInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class NetworkUtilitiesToolApplication implements CommandLineRunner {

    private final CommandLineInterface commandLineInterface;

    public static void main(String[] args) {
        SpringApplication.run(NetworkUtilitiesToolApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            commandLineInterface.execute(args);
        } else {
            commandLineInterface.showHelp();
        }
    }
}
