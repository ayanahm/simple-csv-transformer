package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class ColumnAverageFormatter implements MappedCsvValueToStringFormatter {
  private final static ColumnAverageFormatter instance = new ColumnAverageFormatter();

  public static ColumnAverageFormatter get() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping, Map<String, String> parameters) {
    String[] vals = valueFromCsvMapping.split(",");
    int val1 = Integer.parseInt(vals[0].trim());
    int val2 = Integer.parseInt(vals[1].trim());

    int result = (val1 + val2) / 2;
    return CsvValueToJavaMappingResult.ofValue("" + result);
  }

}
