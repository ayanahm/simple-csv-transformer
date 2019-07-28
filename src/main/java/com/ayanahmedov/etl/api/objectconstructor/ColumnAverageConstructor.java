package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public class ColumnAverageConstructor implements MappedCsvValueStringConstructor {
  private final static ColumnAverageConstructor instance = new ColumnAverageConstructor();

  public static ColumnAverageConstructor get() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    String[] vals = valueFromCsvMapping.split(",");
    int val1 = Integer.parseInt(vals[0].trim());
    int val2 = Integer.parseInt(vals[1].trim());

    int result = (val1 + val2) / 2;
    return CsvValueToJavaMappingResult.ofValue("" + result);
  }

}
