package com.hjusic.recursive.webscrapper.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebsiteQueueManager {

  private final Queue<String> urlQueue = new LinkedList<>();
  private final Set<String> visitedUrls;
  final Predicate<String> eligibilityPredicate;

  public WebsiteQueueManager(String initialUrl, Set<String> visitedUrls, boolean sameScopeOnly) {
    this.visitedUrls = visitedUrls;
    this.eligibilityPredicate = sameScopeOnly ? createSameScopePredicate(initialUrl) : url -> true;

    addUrl(initialUrl);
  }

  private Predicate<String> createSameScopePredicate(String baseUrl) {
    try {
      URI baseUri = new URI(baseUrl);
      String baseHost = baseUri.getHost();

      return url -> {
        try {
          URI uri = new URI(url);
          return baseHost.equalsIgnoreCase(uri.getHost());
        } catch (URISyntaxException e) {
          log.warn("Invalid URL: {}", url);
          return false;
        }
      };
    } catch (URISyntaxException e) {
      log.error("Invalid base URL: {}", baseUrl);
      return url -> false;
    }
  }

  public boolean hasNext() {
    return !urlQueue.isEmpty();
  }

  public String next() {
    return urlQueue.poll();
  }

  public void addUrls(Set<String> urls) {
    Set<String> eligibleUrls = urls.stream()
        .filter(url -> !visitedUrls.contains(url))
        .filter(eligibilityPredicate)
        .collect(Collectors.toSet());

    urlQueue.addAll(eligibleUrls);
  }

  private void addUrl(String url) {
    if (!visitedUrls.contains(url) && eligibilityPredicate.test(url)) {
      urlQueue.offer(url);
    }
  }
}
