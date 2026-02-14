package com.dskroba.app;

import com.dskroba.base.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {"com.dskroba"}
)
public class ExpenseBotApplication {
    public static void main(String[] args) {
        Configuration.loadGlobalProperties();
        SpringApplication.run(ExpenseBotApplication.class, args);
    }
}
