package com.hjusic.url.fuzzing.model;

import com.hjusic.url.fuzzing.infrastructure.FuzzProperties;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class UrlFuzzManager {

  @Autowired
  @Setter
  private FuzzProperties fuzzProperties;

  @Getter
  private ExecutorService executorService;
  @Getter
  private ScheduledExecutorService scheduler;

  public void startFuzzing(FuzzSource source) {
    this.executorService = Executors.newFixedThreadPool(fuzzProperties.getThreads());
    this.scheduler = Executors.newScheduledThreadPool(1);

    long intervalMillis = 1000L / fuzzProperties.getDelay(); // Calculate interval per request

    List<String> urls = source.getValueStream().toList();
    Iterator<String> iterator = urls.iterator();

    if (!iterator.hasNext()) {
      CompletableFuture.completedFuture(null);
      return;
    }

    CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    scheduler.scheduleAtFixedRate(() -> {
      if (iterator.hasNext()) {
        String url = iterator.next();
        executorService.submit(() -> processUrl(fuzzProperties.getBaseUrl() + url));
      } else {
        completionFuture.complete(null);
        shutdown(); // Shutdown once processing is done
      }
    }, 0, intervalMillis, TimeUnit.MILLISECONDS);

  }

  void processUrl(String url) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);

      int responseCode = connection.getResponseCode();
      log.info("{} -> Response: {}", url, responseCode);
    } catch (Exception e) {
      log.error("{} -> ERROR: {}", url, e.getMessage());
    }
  }

  public void shutdown() {
    executorService.shutdown();
    scheduler.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
    }
  }
}
