package com.hjusic.scrapper.common.model;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseWebPage {
  private String url;
  private String content;
  private Map<String, String> headers = new HashMap<>();
  private int statusCode;
  private Map<String, String> cookies = new HashMap<>();
  private Map<String, String> meta = new HashMap<>();
  private String error; // Field to capture error details

  public static BaseWebPage from(String url, String content, int statusCode) {
    return new BaseWebPage(url, content, new HashMap<>(), statusCode, new HashMap<>(), new HashMap<>(), null);
  }
}
