package com.hjusic.mysql.sink.model;

import com.hjusic.scrapper.common.model.BaseWebPage;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class BaseWebsiteEntity {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;

  private String url;
  private int statusCode;
  private String error;

  @Temporal(TemporalType.TIMESTAMP)  // Ensure the timestamp is stored as a Date
  private Date created = new Date();

  @ElementCollection // Persist Map as a collection
  @CollectionTable(name = "website_headers", joinColumns = @JoinColumn(name = "website_id"))
  @MapKeyColumn(name = "header_key")
  @Column(name = "header_value")
  private final Map<String, String> headers = new HashMap<>();

  @ElementCollection
  @CollectionTable(name = "website_cookies", joinColumns = @JoinColumn(name = "website_id"))
  @MapKeyColumn(name = "cookie_key")
  @Column(name = "cookie_value")
  private final Map<String, String> cookies = new HashMap<>();

  @ElementCollection
  @CollectionTable(name = "website_meta", joinColumns = @JoinColumn(name = "website_id"))
  @MapKeyColumn(name = "meta_key")
  @Column(name = "meta_value", length = 65535)
  private final Map<String, String> meta = new HashMap<>();

  private BaseWebsiteEntity(Long id, BaseWebPage baseWebPage) {
    this.id = id;
    this.url = baseWebPage.getUrl();
    this.statusCode = baseWebPage.getStatusCode();
    this.error = baseWebPage.getError();
    this.headers.putAll(baseWebPage.getHeaders());
    this.cookies.putAll(baseWebPage.getCookies());
    this.meta.putAll(baseWebPage.getMeta());
  }

  public BaseWebsiteEntity() {

  }

  public static BaseWebsiteEntity from(BaseWebPage baseWebPage) {
    return new BaseWebsiteEntity(null, baseWebPage);
  }

  public Long getId() {
    return id;
  }

  public String getUrl() {
    return url;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, String> getCookies() {
    return cookies;
  }

  public Map<String, String> getMeta() {
    return meta;
  }
}
