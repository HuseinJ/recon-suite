package com.hjusic.recursive.webscraper.model;

import static org.junit.jupiter.api.Assertions.*;

import com.hjusic.scrapper.common.model.BaseWebPage;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.mockito.Mockito.*;

class RecursiveScraperTest {

  @Mock
  private WebsiteQueueManager mockQueueManager;

  @Mock
  private Connection mockConnection;

  @Mock
  private Connection.Response mockResponse;

  @Mock
  private Document mockDocument;

  private RecursiveScraper scrapper;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.openMocks(this);
    scrapper = new RecursiveScraper("http://example.com", true, new ScrapperProperties());
    // Injecting the mock queueManager
    scrapper = spy(scrapper);
    Field queueManagerField = RecursiveScraper.class.getDeclaredField("queueManager");
    queueManagerField.setAccessible(true);
    queueManagerField.set(scrapper, mockQueueManager);
  }

  @Test
  void testIterator_hasNextReturnsTrueWhenQueueHasElements() {
    when(mockQueueManager.hasNext()).thenReturn(true);
    Iterator<BaseWebPage> iterator = scrapper.iterator();

    assertTrue(iterator.hasNext());
  }

  @Test
  void testIterator_hasNextReturnsFalseWhenQueueIsEmpty() {
    when(mockQueueManager.hasNext()).thenReturn(false);
    Iterator<BaseWebPage> iterator = scrapper.iterator();

    assertFalse(iterator.hasNext());
  }

  @Test
  @Disabled
  void testIterator_nextReturnsBaseWebPage() throws IOException {
    // Mocking the queue manager to return a URL
    String testUrl = "http://example.com/page1";
    when(mockQueueManager.hasNext()).thenReturn(true);
    when(mockQueueManager.next()).thenReturn(testUrl);

    // Mocking Jsoup's connection and response
    when(Jsoup.connect(testUrl)).thenReturn(mockConnection);
    when(mockConnection.ignoreContentType(true)).thenReturn(mockConnection);
    when(mockConnection.maxBodySize(Integer.MAX_VALUE)).thenReturn(mockConnection);
    when(mockConnection.execute()).thenReturn(mockResponse);

    // Mocking the response
    when(mockResponse.url()).thenReturn(new java.net.URL(testUrl));
    when(mockResponse.parse()).thenReturn(mockDocument);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.headers()).thenReturn(Map.of("Header1", "Value1"));
    when(mockResponse.cookies()).thenReturn(Map.of("Cookie1", "Value1"));

    // Mocking the document
    when(mockDocument.toString()).thenReturn("<html></html>");
    Elements mockElements = new Elements(Arrays.asList(new Element("meta").attr("name", "description").attr("content", "test")));
    when(mockDocument.select("meta")).thenReturn(mockElements);

    // Simulating link extraction
    when(mockDocument.select("a[href]")).thenReturn(new Elements());
    doNothing().when(mockQueueManager).addUrls(Set.of());

    // Testing the iterator
    Iterator<BaseWebPage> iterator = scrapper.iterator();
    BaseWebPage page = iterator.next();

    assertNotNull(page);
    assertEquals(testUrl, page.getUrl());
    assertEquals("Value1", page.getHeaders().get("Header1"));
    assertEquals("Value1", page.getCookies().get("Cookie1"));
  }

  @Test
  void testIterator_nextThrowsExceptionWhenNoElements() {
    when(mockQueueManager.hasNext()).thenReturn(false);
    Iterator<BaseWebPage> iterator = scrapper.iterator();

    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  @Disabled
  void testIterator_nextReturnsErrorPageOnException() throws IOException {
    // Mocking the queue manager to return a URL
    String testUrl = "http://example.com/page1";
    when(mockQueueManager.hasNext()).thenReturn(true);
    when(mockQueueManager.next()).thenReturn(testUrl);

    // Mocking Jsoup to throw an exception
    when(Jsoup.connect(testUrl)).thenThrow(new IOException("Connection error"));

    // Testing the iterator
    Iterator<BaseWebPage> iterator = scrapper.iterator();
    BaseWebPage page = iterator.next();

    assertNotNull(page);
    assertEquals(testUrl, page.getUrl());
    assertEquals("Connection error", page.getError());
  }

  @Test
  void testExtractLinks() {
    // Mocking the document with sample links
    Elements mockLinks = new Elements(Arrays.asList(
        new Element("a").attr("href", "http://example.com/link1"),
        new Element("a").attr("href", "https://example.com/link2"),
        new Element("a").attr("href", "/relative-link") // Invalid absolute link
    ));
    when(mockDocument.select("a[href]")).thenReturn(mockLinks);

    List<String> links = scrapper.extractLinks(mockDocument);

    assertNotNull(links);
    assertEquals(2, links.size());
    assertTrue(links.contains("http://example.com/link1"));
    assertTrue(links.contains("https://example.com/link2"));
  }
}
