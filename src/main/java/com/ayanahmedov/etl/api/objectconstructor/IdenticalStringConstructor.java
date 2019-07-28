package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class IdenticalStringConstructor implements MappedCsvValueStringConstructor {
  private static final IdenticalStringConstructor instance = new IdenticalStringConstructor();

  public static IdenticalStringConstructor of() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    return CsvValueToJavaMappingResult.ofValue(valueFromCsvMapping);
  }
}
