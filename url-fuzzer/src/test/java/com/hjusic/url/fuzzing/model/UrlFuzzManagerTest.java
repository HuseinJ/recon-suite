package com.hjusic.url.fuzzing.model;

import com.hjusic.url.fuzzing.infrastructure.FuzzProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlFuzzManagerTest {

  @Mock
  private FuzzSource fuzzSource;

  @Mock
  private FuzzProperties fuzzProperties;

  private UrlFuzzManager fuzzManager;

  @BeforeEach
  void setUp() {
    when(fuzzProperties.getThreads()).thenReturn(10);
    when(fuzzProperties.getDelay()).thenReturn(2); // 2 requests per second

    fuzzManager = new UrlFuzzManager();
    ReflectionTestUtils.setField(fuzzManager, "fuzzProperties", fuzzProperties);
  }

  @Test
  void testFuzzingProcessesUrls() throws InterruptedException {
    when(fuzzProperties.getBaseUrl()).thenReturn("https://example.com/");
    when(fuzzSource.getValueStream()).thenReturn(Stream.of("test1", "test2", "test3"));

    fuzzManager.startFuzzing(fuzzSource);

    // Wait for fuzzing tasks to process
    TimeUnit.SECONDS.sleep(2);

    verify(fuzzSource, times(1)).getValueStream();
  }

  @Test
  void testRateLimitingWorks() throws InterruptedException {
    when(fuzzProperties.getBaseUrl()).thenReturn("https://example.com/");
    // Given: A fuzz source with multiple URLs
    when(fuzzSource.getValueStream()).thenReturn(Stream.of("url1", "url2", "url3", "url4", "url5"));

    // Create a real instance and spy on it
    UrlFuzzManager spyFuzzManager = Mockito.spy(new UrlFuzzManager());
    spyFuzzManager.setFuzzProperties(fuzzProperties);

    // Capture timestamps when processUrl() is executed
    List<Long> timestamps = Collections.synchronizedList(new ArrayList<>());

    // Spy on processUrl() to record the time of each call
    doAnswer(invocation -> {
      timestamps.add(System.nanoTime()); // Capture time in nanoseconds
      return null;
    }).when(spyFuzzManager).processUrl(anyString());

    // When: Starting fuzzing
    spyFuzzManager.startFuzzing(fuzzSource);

    // Allow enough time for processing
    TimeUnit.SECONDS.sleep(3);

    // Verify that the rate limiter is enforced
    assertTrue(timestamps.size() > 1, "At least two requests should have been made");

    // Compute time differences between consecutive requests
    for (int i = 1; i < timestamps.size(); i++) {
      long intervalMillis = TimeUnit.NANOSECONDS.toMillis(timestamps.get(i) - timestamps.get(i - 1));
      assertTrue(intervalMillis >= 490, "Requests should be spaced at least ~500ms apart (2 req/sec)");
    }
  }

  @Test
  void testShutdownProperlyStopsThreads() {
    when(fuzzSource.getValueStream()).thenReturn(Stream.of("test1"));

    fuzzManager.startFuzzing(fuzzSource);
    fuzzManager.shutdown();

    assertTrue(
        fuzzManager.getExecutorService().isShutdown(),
        "ExecutorService should be shut down"
    );
    assertTrue(
        fuzzManager.getScheduler().isShutdown(),
        "Scheduler should be shut down"
    );
  }
}
