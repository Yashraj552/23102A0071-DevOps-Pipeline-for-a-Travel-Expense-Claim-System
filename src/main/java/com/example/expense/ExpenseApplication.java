package com.example.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Entry point. Extending SpringBootServletInitializer lets the same artifact
 * run on external Tomcat (WAR) and with "java -jar" (Docker).
 */
@SpringBootApplication
public class ExpenseApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ExpenseApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ExpenseApplication.class, args);
    }
}
