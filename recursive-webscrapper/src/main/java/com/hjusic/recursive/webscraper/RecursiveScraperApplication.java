package com.hjusic.recursive.webscraper;

import com.hjusic.recursive.webscraper.model.ScrapperProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ScrapperProperties.class)
@SpringBootApplication
public class RecursiveScraperApplication {
  public static void main(String[] args) {
    SpringApplication.run(RecursiveScraperApplication.class, args);
  }
}
