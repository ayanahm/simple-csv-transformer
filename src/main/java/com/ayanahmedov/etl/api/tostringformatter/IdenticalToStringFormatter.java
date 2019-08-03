package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class IdenticalToStringFormatter implements MappedCsvValueToStringFormatter {
  private static final IdenticalToStringFormatter instance = new IdenticalToStringFormatter();

  public static IdenticalToStringFormatter get() {
    return instance;
  }

  @Override
  public MappedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    return this;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    return CsvValueToJavaMappingResult.ofValue(valueFromCsvMapping);
  }
}
