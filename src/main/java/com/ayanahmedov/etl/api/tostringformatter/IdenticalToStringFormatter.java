package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class IdenticalToStringFormatter implements MappedCsvValueToStringFormatter {
  private static final IdenticalToStringFormatter instance = new IdenticalToStringFormatter();

  public static IdenticalToStringFormatter of() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping, Map<String, String> parameters) {
    return CsvValueToJavaMappingResult.ofValue(valueFromCsvMapping);
  }
}
