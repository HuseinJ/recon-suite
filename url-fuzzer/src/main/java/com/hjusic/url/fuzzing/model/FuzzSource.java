package com.hjusic.url.fuzzing.model;

import java.util.stream.Stream;

public interface FuzzSource {
  Stream<String> getValueStream();
}
