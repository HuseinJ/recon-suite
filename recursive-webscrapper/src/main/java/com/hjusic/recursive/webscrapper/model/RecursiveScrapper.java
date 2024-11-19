package com.hjusic.recursive.webscrapper.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Log4j2
public class RecursiveScrapper implements Iterable<BaseWebPage> {

  private final String urlToScrapp;
  private final Set<String> visitedUrls;

  public RecursiveScrapper(String urlToScrapp) {
    this.urlToScrapp = urlToScrapp;
    this.visitedUrls = new HashSet<>();
  }

  private BaseWebPage getBase(Response response, Document document) throws IOException {
    var basePage = new BaseWebPage(response.url().toString(), document.toString(), response.statusCode());
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

  private List<String> extractLinks(Document doc) {
    Elements links = doc.select("a[href]");
    return links.stream()
        .map(link -> link.absUrl("href")) // Get absolute URL
        .filter(url -> url.startsWith("http")) // Only keep valid HTTP/HTTPS URLs
        .collect(Collectors.toList());
  }

  public Iterator<BaseWebPage> iterator() {
    return new Iterator<>() {
      private final Queue<String> urlQueue = new LinkedList<>(List.of(urlToScrapp));

      @Override
      public boolean hasNext() {
        return !urlQueue.isEmpty();
      }

      @Override
      public BaseWebPage next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more pages to scrape.");
        }

        String currentUrl = urlQueue.poll();
        if (visitedUrls.contains(currentUrl)) {
          if(hasNext()){
            return next();
          } else {
            throw new NoSuchElementException("No more pages to scrape.");
          }
        }

        Response response = null;

        try {
          assert currentUrl != null;
          response = Jsoup.connect(currentUrl).ignoreContentType(true).execute();
          visitedUrls.add(currentUrl);

          Document doc = response.parse();
          List<String> newLinks = extractLinks(doc);

          newLinks.stream()
              .filter(link -> !visitedUrls.contains(link))
              .forEach(urlQueue::offer);

          return getBase(response, doc);
        } catch (IOException e) {
          log.error("Failed to scrape {} returned ", currentUrl);
          if (response == null) {
            return new BaseWebPage(currentUrl, "", 0);
          } else {
            return new BaseWebPage(currentUrl, "", response.statusCode());
          }
        }
      }
    };
  }
}