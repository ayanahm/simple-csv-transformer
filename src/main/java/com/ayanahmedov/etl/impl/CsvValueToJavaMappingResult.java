package com.ayanahmedov.etl.impl;

import java.util.Optional;

public class CsvValueToJavaMappingResult {

  private String value;
  private Exception exception;

  public static CsvValueToJavaMappingResult ofMappingError(Exception e) {
    CsvValueToJavaMappingResult r = new CsvValueToJavaMappingResult();
    r.exception = e;
    return r;
  }

  public static CsvValueToJavaMappingResult ofValue(String value) {
    CsvValueToJavaMappingResult r = new CsvValueToJavaMappingResult();
    r.value = value;
    return r;
  }

  public Optional<String> getValue() {
    return Optional.ofNullable(value);
  }

  public Optional<Exception> getException() {
    return Optional.ofNullable(exception);
  }
}
