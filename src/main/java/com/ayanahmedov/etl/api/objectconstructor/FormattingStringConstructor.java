package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.IllegalFormatException;
import java.util.Map;

/**
 * The pattern for this must contain only %s place holders.
 * using %d, for example, would fail.
 * since currently, no conversion from csv values are done.
 * They are simply passed as Strings.
 */
public class FormattingStringConstructor implements MappedCsvValueStringConstructor {
  private static final FormattingStringConstructor instance = new FormattingStringConstructor();

  public static FormattingStringConstructor get() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    String[] split = valueFromCsvMapping.split(",");
    String stringFormat = parameters.get("string-format");
    if (null == stringFormat) {
      throw DslConfigurationException.STRING_FORMATTER_CONSTRUCTOR_REQUIRES_PARAMETER_FORMAT;
    }
    try {
      String result = String.format(stringFormat, (Object[]) split);
      return CsvValueToJavaMappingResult.ofValue(result);
    } catch (IllegalFormatException e) {
      throw DslConfigurationException.STRING_FORMATTER_INCORRECT.withException(e);
    }
  }
}
