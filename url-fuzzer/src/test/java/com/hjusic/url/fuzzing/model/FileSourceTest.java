package com.hjusic.url.fuzzing.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileSourceTest {

  private FileSource fileSource;

  @TempDir
  Path tempDir;

  private Path testFile;

  @BeforeEach
  void setUp() throws IOException {
    testFile = tempDir.resolve("test-inputs.txt");
    Files.write(testFile, List.of("fuzz1", "fuzz2", "fuzz3"));
    fileSource = new FileSource(testFile.toString());
  }

  @Test
  void testGetFuzzInputs_ReturnsCorrectData() {
    try (Stream<String> stream = fileSource.getValueStream()) {
      List<String> lines = stream.collect(Collectors.toList());
      assertEquals(List.of("fuzz1", "fuzz2", "fuzz3"), lines);
    }
  }

  @Test
  void testGetFuzzInputs_EmptyFileReturnsEmptyStream() throws IOException {
    Path emptyFile = tempDir.resolve("empty.txt");
    Files.createFile(emptyFile);
    FileSource emptySource = new FileSource(emptyFile.toString());

    try (Stream<String> stream = emptySource.getValueStream()) {
      assertTrue(stream.findAny().isEmpty());
    }
  }

  @Test
  void testGetFuzzInputs_FileNotFoundThrowsException() {
    FileSource invalidSource = new FileSource("non_existent_file.txt");

    Exception exception = assertThrows(RuntimeException.class, invalidSource::getValueStream);
    assertTrue(exception.getMessage().contains("Failed to read fuzzing inputs from file"));
  }
}
