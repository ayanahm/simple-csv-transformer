package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class ColumnAverageFormatter implements ReducedCsvValueToStringFormatter {
  private final static ColumnAverageFormatter instance = new ColumnAverageFormatter();

  private ColumnAverageFormatter(){}

  public static ColumnAverageFormatter get() {
    return instance;
  }

  @Override
  public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    //no params required
    return this;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    String[] values = valueFromCsvMapping.split(",");
    int val1 = Integer.parseInt(values[0].trim());
    int val2 = Integer.parseInt(values[1].trim());

    int result = (val1 + val2) / 2;
    return CsvValueToJavaMappingResult.ofValue("" + result);
  }

}
