package com.hjusic.recursive.webscraper.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for configuring the web scrapper.
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "scrapper")
public class ScrapperProperties {

  // Getters and setters
  /**
   * URL to start scrapping from.
   */
  private String url;

  /**
   * Delay between requests in milliseconds.
   */
  private int delay;

  /**
   * If true, only URLs from the same scope as the initial URL will be visited.
   */
  private boolean sameScope;

  /**
   * Maximum body size in bytes.
   */
  private int maxBodySize;

}
