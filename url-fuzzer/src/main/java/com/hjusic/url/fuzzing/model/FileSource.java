package com.hjusic.url.fuzzing.model;

import java.util.stream.Stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of FuzzSource that reads fuzzing inputs from a file.
 */
public class FileSource implements FuzzSource {

  private final Path filePath;

  /**
   * Constructs a FileSource with the given file path.
   *
   * @param filePath The path to the file containing fuzzing inputs.
   */
  public FileSource(String filePath) {
    this.filePath = Path.of(filePath);
  }

  /**
   * Reads the file and returns a stream of fuzzing inputs.
   *
   * @return Stream of fuzzing inputs as strings.
   */
  @Override
  public Stream<String> getValueStream() {
    try {
      return Files.lines(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read fuzzing inputs from file: " + filePath, e);
    }
  }
}

