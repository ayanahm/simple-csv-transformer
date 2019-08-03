package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class IdenticalToStringFormatter implements MappedCsvValueToStringFormatter {
  private static final IdenticalToStringFormatter instance = new IdenticalToStringFormatter();

  public static IdenticalToStringFormatter of() {
    return instance;
  }

  @Override
  public void init(Map<String, String> parameters) {
    //no params required
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    return CsvValueToJavaMappingResult.ofValue(valueFromCsvMapping);
  }
}
