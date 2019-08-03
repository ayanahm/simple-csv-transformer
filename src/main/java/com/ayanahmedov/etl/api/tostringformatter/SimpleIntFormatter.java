package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;
import java.util.regex.Pattern;

public class SimpleIntFormatter implements ReducedCsvValueToStringFormatter {
  private static final SimpleIntFormatter instance = new SimpleIntFormatter();
  private static final Pattern intPattern = Pattern.compile("\\d+");

  public static SimpleIntFormatter get() {

    return instance;
  }

  @Override
  public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    return this;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    String trimmed = valueFromCsvMapping.trim();
    if (!intPattern.matcher(trimmed).matches()) {
      return CsvValueToJavaMappingResult.ofMappingError(new NumberFormatException("Cannot parse to int. The value=" + trimmed));
    }
    return CsvValueToJavaMappingResult.ofValue(trimmed);
  }
}
