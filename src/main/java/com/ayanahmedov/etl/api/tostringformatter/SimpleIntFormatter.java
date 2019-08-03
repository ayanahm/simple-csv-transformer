package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class SimpleIntFormatter implements MappedCsvValueToStringFormatter {
  private static final SimpleIntFormatter instance = new SimpleIntFormatter();

  public static SimpleIntFormatter of() {

    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping, Map<String, String> parameters) {
    try {
      int val = Integer.parseInt(valueFromCsvMapping.trim());
      return CsvValueToJavaMappingResult.ofValue("" + val);
    } catch (NumberFormatException e) {
      return CsvValueToJavaMappingResult.ofMappingError(e);
    }
  }
}
