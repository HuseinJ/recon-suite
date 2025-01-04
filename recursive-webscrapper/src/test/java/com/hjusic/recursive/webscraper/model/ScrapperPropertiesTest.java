package com.hjusic.recursive.webscraper.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScrapperPropertiesTest {

  @Test
  void testInitializeObject() {
    ScrapperProperties properties = new ScrapperProperties();
    properties.setUrl("https://example.com");
    properties.setDelay(1000);
    properties.setSameScope(true);

    assertNotNull(properties);
    assertEquals("https://example.com", properties.getUrl());
    assertEquals(1000, properties.getDelay());
    assertTrue(properties.isSameScope());
  }

  
}