package com.hjusic.recursive.webscraper.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class BaseWebpageSender {

  @Autowired
  private ScrapperProperties scrapperProperties;

  @Bean
  public Supplier<BaseWebPage> sendEvents() {
    var scrapper = new RecursiveScraper(scrapperProperties.getUrl(),
        scrapperProperties.isSameScope(), scrapperProperties);
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
