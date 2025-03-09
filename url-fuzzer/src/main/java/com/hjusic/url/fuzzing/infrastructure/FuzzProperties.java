package com.hjusic.url.fuzzing.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "fuzz")
public class FuzzProperties {

  /**
   * URL to start scrapping from.
   */
  private String baseUrl;

  /**
   * Count of threads to use for fuzzing.
   */
  private Integer threads;

  /**
   * Delay between requests in requests/Second.
   */
  private Integer delay;

}
