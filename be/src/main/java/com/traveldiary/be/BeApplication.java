package com.traveldiary.be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Objects;

@SpringBootApplication
public class BeApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("DATASOURCE_URL", Objects.requireNonNull(dotenv.get("DATASOURCE_URL")));
        System.setProperty("DATASOURCE_USERNAME", Objects.requireNonNull(dotenv.get("DATASOURCE_USERNAME")));
        System.setProperty("DATASOURCE_PASSWORD", Objects.requireNonNull(dotenv.get("DATASOURCE_PASSWORD")));

        SpringApplication.run(BeApplication.class, args);
    }
}
