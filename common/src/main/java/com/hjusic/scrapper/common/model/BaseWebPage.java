package com.hjusic.scrapper.common.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Value;

@Value
public class BaseWebPage {
  String url;
  String content;
  Map<String, String> headers = new HashMap<>();
  int statusCode;
  Map<String, String> cookies = new HashMap<>();
  Map<String, String> meta = new HashMap<>();
}
