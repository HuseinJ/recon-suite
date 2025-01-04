package com.hjusic.mysql.sink.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseWebsiteMysqlSinkConfiguration {

  @Bean
  public Consumer<BaseWebPage> process(BaseWebsiteEntityRepository repository) {
    return baseWebPage -> {
      repository.save(BaseWebsiteEntity.from(baseWebPage));
    };
  }
}
