package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Formats the input string values by standard java formatter.
 *
 * The pattern for this must contain only %s place holders.
 * using %d, for example, would fail.
 * since currently, no conversion other then string from csv values are done.
 * They are simply passed as Strings.
 */
public class FormattingToStringFormatter implements MappedCsvValueToStringFormatter {
  private static final FormattingToStringFormatter instance = new FormattingToStringFormatter();

  public static FormattingToStringFormatter get() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping, Map<String, String> parameters) {
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
