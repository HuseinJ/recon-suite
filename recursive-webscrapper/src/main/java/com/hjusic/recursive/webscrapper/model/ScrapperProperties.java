package com.hjusic.recursive.webscrapper.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for configuring the web scrapper.
 */
@Component
@ConfigurationProperties(prefix = "scrapper")
public class ScrapperProperties {

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
  private boolean samescope;

  // Getters and setters
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public boolean isSamescope() {
    return samescope;
  }

  public void setSamescope(boolean samescope) {
    this.samescope = samescope;
  }
}
