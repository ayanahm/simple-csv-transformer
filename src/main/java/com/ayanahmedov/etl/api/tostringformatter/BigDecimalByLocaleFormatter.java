package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BigDecimalByLocaleFormatter implements MappedCsvValueToStringFormatter {
  public static final String PARAM_SOURCE_NUMBER_LOCALE = "locale";

  private static final ThreadLocal<Map<Locale, NumberFormat>> numberFormatPerLocale =
      ThreadLocal.withInitial(HashMap::new);

  private String sourceNumberLocale = null;

  private BigDecimalByLocaleFormatter() {
  }

  public static BigDecimalByLocaleFormatter get() {
    return new BigDecimalByLocaleFormatter();
  }

  @Override
  public void init(Map<String, String> parameters) {
    this.sourceNumberLocale = parameters.get(PARAM_SOURCE_NUMBER_LOCALE);
    if (sourceNumberLocale == null) {
      throw DslConfigurationException.BIG_DECIMAL_CONSTRUCTOR_REQUIRES_PARAMETER_LOCALE;
    }
  }

  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    Locale locale = Locale.forLanguageTag(sourceNumberLocale);
    NumberFormat nf = getNumberFormat(locale);
    try {
      Number parsedNum = nf.parse(valueFromCsvMapping);
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
