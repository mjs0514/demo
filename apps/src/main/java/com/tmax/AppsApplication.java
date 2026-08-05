package com.tmax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppsApplication {

    public static void main(String[] args) {
        if (VersionCommand.isVersionRequest(args)) {
            VersionCommand.printVersion(args, AppsApplication.class);
            return;
        }

        SpringApplication.run(AppsApplication.class, args);
    }
}
