package com.hjusic.recursive.webscrapper;

import com.hjusic.recursive.webscrapper.model.ScrapperProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ScrapperProperties.class)
@SpringBootApplication
public class RecursiveScrapperApplication {
  public static void main(String[] args) {
    SpringApplication.run(RecursiveScrapperApplication.class, args);
  }
}
