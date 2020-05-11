package com.salesforce.streaming.api.main;

import com.salesforce.streaming.api.SalesforceStreamingApiConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(SalesforceStreamingApiConfiguration.class)
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
