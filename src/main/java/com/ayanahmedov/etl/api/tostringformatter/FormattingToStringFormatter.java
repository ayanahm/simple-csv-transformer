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
public class FormattingToStringFormatter implements ReducedCsvValueToStringFormatter {
  public static final String PARAM_TARGET_STRING_FORMAT = "string-format";

  private String stringFormat;

  private FormattingToStringFormatter() {
  }

  public static FormattingToStringFormatter getUninitialized() {
    return new FormattingToStringFormatter();
  }


  @Override
  public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    FormattingToStringFormatter instance = getUninitialized();
    instance.stringFormat = parameters.get(PARAM_TARGET_STRING_FORMAT);
    if (null == instance.stringFormat) {
      throw DslConfigurationException.STRING_FORMATTER_REQUIRES_PARAMETER_FORMAT;
    }
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    String[] split = valueFromCsvMapping.split(",");

    try {
      String result = String.format(stringFormat, (Object[]) split);
      return CsvValueToJavaMappingResult.ofValue(result);
    } catch (IllegalFormatException e) {
      throw DslConfigurationException.STRING_FORMATTER_INCORRECT
          .withAdditionalMessage("String format was " + stringFormat + ". ")
          .withException(e);
    }
  }

}
