package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class SimpleIntConstructor implements MappedCsvValueStringConstructor {
  private static final SimpleIntConstructor instance = new SimpleIntConstructor();

  public static SimpleIntConstructor of() {

    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    try {
      int val = Integer.parseInt(valueFromCsvMapping.trim());
      return CsvValueToJavaMappingResult.ofValue("" + val);
    } catch (NumberFormatException e) {
      return CsvValueToJavaMappingResult.ofMappingError(e);
    }
  }
}
