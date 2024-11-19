package com.hjusic.scrapper.common.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BaseWebPageTest {

  @Test
  void testBaseWebPageCreation() {
    String url = "http://example.com";
    String content = "<html></html>";
    int statusCode = 200;

    BaseWebPage page = new BaseWebPage(url, content, statusCode);

    assertEquals(url, page.getUrl());
    assertEquals(content, page.getContent());
    assertEquals(statusCode, page.getStatusCode());
    assertNotNull(page.getHeaders());
    assertTrue(page.getHeaders().isEmpty());
    assertNotNull(page.getCookies());
    assertTrue(page.getCookies().isEmpty());
    assertNotNull(page.getMeta());
    assertTrue(page.getMeta().isEmpty());
  }

}