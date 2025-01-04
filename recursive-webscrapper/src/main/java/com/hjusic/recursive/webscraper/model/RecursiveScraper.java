package com.hjusic.recursive.webscraper.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Log4j2
public class RecursiveScraper implements Iterable<BaseWebPage> {

  private final Set<String> visitedUrls;
  private final WebsiteQueueManager queueManager;
  private ScrapperProperties scrapperProperties;

  public RecursiveScraper(String urlToScrapp, boolean sameScopeOnly, ScrapperProperties scrapperProperties) {
    this.visitedUrls = new HashSet<>();
    this.queueManager = new WebsiteQueueManager(urlToScrapp, visitedUrls, sameScopeOnly);
    this.scrapperProperties = scrapperProperties;
  }

  private BaseWebPage getBase(Response response, Document document) throws IOException {
    var basePage = BaseWebPage.from(response.url().toString(), response.statusCode());
    basePage.getHeaders().putAll(response.headers());
    basePage.getMeta().putAll(document.select("meta").stream().collect(
        Collectors.toMap(
            e -> e.attr("name"),
            e -> e.attr("content"),
            (existing, replacement) -> existing + ", " + replacement // Concatenate values
        )
    ));
    basePage.getCookies().putAll(response.cookies());
    return basePage;
  }

  List<String> extractLinks(Document doc) {
    Elements links = doc.select("a[href]");
    return links.stream()
        .map(link -> link.absUrl("href")) // Get absolute URL
        .filter(url -> url.startsWith("http")) // Only keep valid HTTP/HTTPS URLs
        .collect(Collectors.toList());
  }

  @Override
  public Iterator<BaseWebPage> iterator() {
    return new Iterator<>() {

      @Override
      public boolean hasNext() {
        return queueManager.hasNext();
      }

      @Override
      public BaseWebPage next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more pages to scrape.");
        }

        String currentUrl = queueManager.next();
        visitedUrls.add(currentUrl);

        try {
          Response response = Jsoup.connect(currentUrl).ignoreContentType(true).maxBodySize(Integer.MAX_VALUE).execute();
          Document doc = response.parse();

          // Extract and queue new links
          List<String> newLinks = extractLinks(doc);
          queueManager.addUrls(Set.copyOf(newLinks));

          return getBase(response, doc);
        } catch (Exception e) {
          log.error("Failed to scrape {}: {}", currentUrl, e.getMessage());
          // Return a BaseWebPage with error information
          BaseWebPage errorPage = new BaseWebPage();
          errorPage.setUrl(currentUrl);
          errorPage.setError(e.getMessage());
          if (e instanceof HttpStatusException) {
            errorPage.setStatusCode(((HttpStatusException) e).getStatusCode());
          } else {
            errorPage.setStatusCode(0); // Indicate an unknown status code
          }
          return errorPage;
        }
      }
    };
  }
}
