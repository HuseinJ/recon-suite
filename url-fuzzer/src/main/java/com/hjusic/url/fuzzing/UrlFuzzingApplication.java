package com.hjusic.url.fuzzing;

import com.hjusic.url.fuzzing.infrastructure.FuzzProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(FuzzProperties.class)
@SpringBootApplication
public class UrlFuzzingApplication {

  public static void main(String[] args) {
    SpringApplication.run(UrlFuzzingApplication.class, args);
  }

}
