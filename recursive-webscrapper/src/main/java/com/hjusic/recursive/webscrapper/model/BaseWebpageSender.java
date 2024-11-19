package com.hjusic.recursive.webscrapper.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class BaseWebpageSender {

  @Value("${scrapper.url}")
  private String urlToScrapp; // Your starting URL

  @Value("${scrapper.delay:1000}") // Default delay is 1000ms (1 second) if not specified
  private long delayInMillis;

  @Bean
  public Supplier<BaseWebPage> sendEvents() {
    var scrapper = new RecursiveScrapper(urlToScrapp);
    var iterator = scrapper.iterator();

    return () -> {
      try {
        if (iterator.hasNext()) {
          var page = iterator.next();
          if (page.getError() != null) {
            log.warn("Error scraping {}: {}", page.getUrl(), page.getError());
          } else {
            log.info("Successfully scraped page: {}", page.getUrl());
          }
          return page;
        } else {
          return null;
        }
      } catch (Exception e) {
        log.error("Unexpected error in scraping workflow", e);
        return null;
      }
    };
  }
}
