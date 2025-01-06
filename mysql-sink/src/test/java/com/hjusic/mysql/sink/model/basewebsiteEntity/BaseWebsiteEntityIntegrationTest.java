package com.hjusic.mysql.sink.model.basewebsiteEntity;

import com.hjusic.mysql.sink.model.basewebsiteentity.BaseWebsiteEntity;
import com.hjusic.mysql.sink.model.basewebsiteentity.BaseWebsiteEntityRepository;
import com.hjusic.scrapper.common.model.BaseWebPage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class BaseWebsiteEntityIntegrationTest {

  @Container
  @ServiceConnection
  static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
      .withDatabaseName("testdb")
      .withUsername("testuser")
      .withPassword("testpass");

  @Autowired
  private BaseWebsiteEntityRepository repository;

  // Dynamically set Spring datasource properties for the Testcontainers MySQL instance
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
  }

  @Test
  @Transactional
  void testSaveAndRetrieveBaseWebsiteEntity() {
    var baseWebPage = BaseWebPage.from("https://example.com", 200);
    baseWebPage.getHeaders().put("Content-Type", "text/html");
    baseWebPage.getCookies().put("session", "abc123");
    baseWebPage.getMeta().put("author", "example_author");
    // Create a new entity
    BaseWebsiteEntity entity = BaseWebsiteEntity.from(baseWebPage);

    // Save the entity
    BaseWebsiteEntity savedEntity = repository.save(entity);

    // Assert that it was saved and has an ID
    assertThat(savedEntity.getId()).isNotNull();

    // Retrieve the entity by ID
    BaseWebsiteEntity retrievedEntity = repository.findById(savedEntity.getId()).orElse(null);

    // Assert that the retrieved entity matches the saved one
    assertThat(retrievedEntity).isNotNull();
    assertThat(retrievedEntity.getUrl()).isEqualTo("https://example.com");
    assertThat(retrievedEntity.getHeaders()).containsEntry("Content-Type", "text/html");
    assertThat(retrievedEntity.getCookies()).containsEntry("session", "abc123");
    assertThat(retrievedEntity.getMeta()).containsEntry("author", "example_author");
  }
}
