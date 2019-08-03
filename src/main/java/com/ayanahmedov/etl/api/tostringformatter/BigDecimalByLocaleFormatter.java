package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BigDecimalByLocaleFormatter implements ReducedCsvValueToStringFormatter {
  public static final String PARAM_SOURCE_NUMBER_LOCALE = "source-locale";
  public static final String PARAM_TARGET_NUMBER_LOCALE = "target-locale";

  private static final ThreadLocal<Map<Locale, NumberFormat>> numberFormatPerLocale =
      ThreadLocal.withInitial(HashMap::new);

  private NumberFormat sourceNumberFormat = null;
  private NumberFormat targetNumberFormat = null;

  private BigDecimalByLocaleFormatter() {
  }

  public static BigDecimalByLocaleFormatter getUninitialized() {
    return new BigDecimalByLocaleFormatter();
  }

  @Override
  public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    BigDecimalByLocaleFormatter instance = getUninitialized();
    String sourceNumberLocale = parameters.get(PARAM_SOURCE_NUMBER_LOCALE);
    if (null == sourceNumberLocale) {
      throw DslConfigurationException.BIG_DECIMAL_CONSTRUCTOR_REQUIRES_PARAMETER_LOCALE;
    }
    Locale sourceLocale = Locale.forLanguageTag(sourceNumberLocale);
    instance.sourceNumberFormat = getNumberFormat(sourceLocale);

    String targetNumberLocale = parameters.get(PARAM_TARGET_NUMBER_LOCALE);
    if (null != targetNumberLocale) {
      Locale targetLocale = Locale.forLanguageTag(targetNumberLocale);
      instance.targetNumberFormat = getNumberFormat(targetLocale);
    }
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    try {
      Number parsedNum = sourceNumberFormat.parse(valueFromCsvMapping);
      BigDecimal result = new BigDecimal(parsedNum.toString());

      if (targetNumberFormat == null) {
        return CsvValueToJavaMappingResult.ofValue(result.toString());
      } else {
        return CsvValueToJavaMappingResult.ofValue(targetNumberFormat.format(result));
      }
    } catch (ParseException e) {
      return CsvValueToJavaMappingResult.ofMappingError(e);
    }
  }

  private NumberFormat getNumberFormat(Locale locale) {
    NumberFormat fmt = numberFormatPerLocale.get().get(locale);
    if (null != fmt) {
      return fmt;
    } else {
      NumberFormat instance = NumberFormat.getInstance(locale);
      numberFormatPerLocale.get().put(locale, instance);
      return instance;
    }
  }

}
