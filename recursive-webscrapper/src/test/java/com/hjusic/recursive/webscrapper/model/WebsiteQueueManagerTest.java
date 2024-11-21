package com.hjusic.recursive.webscrapper.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebsiteQueueManagerTest {

  private WebsiteQueueManager manager;
  private Set<String> visitedUrls;

  @BeforeEach
  void setUp() {
    visitedUrls = new HashSet<>();
  }

  @Test
  void testInitializationWithValidUrl() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    assertTrue(manager.hasNext(), "Initial URL should be present in the queue");
    assertEquals("https://example.com", manager.next(), "Initial URL should match the provided URL");
  }

  @Test
  void testAddUrlsWithinSameScope() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    Set<String> newUrls = Set.of(
        "https://example.com/page1",
        "https://example.com/page2",
        "https://another.com"
    );
    manager.addUrls(newUrls);

    assertTrue(manager.hasNext(), "Queue should contain URLs within the same scope");
    assertEquals("https://example.com", manager.next(), "Queue should maintain same-scope URLs");
    assertEquals("https://example.com/page1", manager.next(), "Queue should maintain same-scope URLs");
    assertEquals("https://example.com/page2", manager.next(), "Queue should maintain same-scope URLs");
    assertFalse(manager.hasNext(), "Queue should not contain out-of-scope URLs");
  }

  @Test
  void testAddUrlsWithVisitedUrls() {
    visitedUrls.add("https://example.com/page1");
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    Set<String> newUrls = Set.of(
        "https://example.com/page1", // Already visited
        "https://example.com/page2"
    );
    manager.addUrls(newUrls);
    assertEquals("https://example.com", manager.next());


    assertTrue(manager.hasNext(), "Queue should skip visited URLs");
    assertEquals("https://example.com/page2", manager.next(), "Queue should only add unvisited URLs");
    assertFalse(manager.hasNext(), "Queue should be empty after consuming remaining URLs");
  }

  @Test
  void testAddUrlsWithNoScopeRestriction() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, false);

    Set<String> newUrls = Set.of(
        "https://example.com/page1",
        "https://another.com/page2"
    );
    manager.addUrls(newUrls);

    assertTrue(manager.hasNext(), "Queue should accept all URLs with no scope restriction");
    assertEquals("https://example.com", manager.next(), "Queue should maintain order of added URLs");
    assertEquals("https://example.com/page1", manager.next(), "Queue should maintain order of added URLs");
    assertEquals("https://another.com/page2", manager.next(), "Queue should include out-of-scope URLs");
  }

  @Test
  void testEligibilityPredicateWithSameScope() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    assertTrue(manager.eligibilityPredicate.test("https://example.com/page1"), "URL within same scope should be eligible");
    assertFalse(manager.eligibilityPredicate.test("https://another.com/page1"), "URL outside scope should not be eligible");
  }

  @Test
  void testEligibilityPredicateWithInvalidUrl() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    assertFalse(manager.eligibilityPredicate.test("invalid-url"), "Invalid URL should not be eligible");
  }

  @Test
  void testNextAndHasNext() {
    manager = new WebsiteQueueManager("https://example.com", visitedUrls, true);

    Set<String> newUrls = Set.of("https://example.com/page1", "https://example.com/page2");
    manager.addUrls(newUrls);

    assertTrue(manager.hasNext(), "Queue should have URLs");
    assertEquals("https://example.com", manager.next(), "Next should return the initial URL");
    assertEquals("https://example.com/page1", manager.next(), "Next should return the first added URL");
    assertEquals("https://example.com/page2", manager.next(), "Next should return the next URL");
    assertFalse(manager.hasNext(), "Queue should be empty after consuming all URLs");
  }


}