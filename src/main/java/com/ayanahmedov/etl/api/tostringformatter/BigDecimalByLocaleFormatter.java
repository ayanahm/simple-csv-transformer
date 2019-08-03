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
  public static final String PARAM_SOURCE_NUMBER_LOCALE = "locale";

  private static final ThreadLocal<Map<Locale, NumberFormat>> numberFormatPerLocale =
      ThreadLocal.withInitial(HashMap::new);

  private String sourceNumberLocale = null;
  private NumberFormat numberFormat = null;

  private BigDecimalByLocaleFormatter() {
  }

  public static BigDecimalByLocaleFormatter getUninitialized() {
    return new BigDecimalByLocaleFormatter();
  }

  @Override
  public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
    BigDecimalByLocaleFormatter instance = getUninitialized();
    instance.sourceNumberLocale = parameters.get(PARAM_SOURCE_NUMBER_LOCALE);
    if (null == instance.sourceNumberLocale) {
      throw DslConfigurationException.BIG_DECIMAL_CONSTRUCTOR_REQUIRES_PARAMETER_LOCALE;
    }
    Locale locale = Locale.forLanguageTag(instance.sourceNumberLocale);
    instance.numberFormat = getNumberFormat(locale);
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    try {
      Number parsedNum = numberFormat.parse(valueFromCsvMapping);
      BigDecimal result = new BigDecimal(parsedNum.toString());
      return CsvValueToJavaMappingResult.ofValue(result.toString());
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
