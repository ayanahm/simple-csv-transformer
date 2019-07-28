package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BigDecimalByLocaleConstructor implements MappedCsvValueStringConstructor {
  private static final BigDecimalByLocaleConstructor instance = new BigDecimalByLocaleConstructor();
  private static final ThreadLocal<Map<Locale, NumberFormat>> numberFormatPerLocale =
      ThreadLocal.withInitial(HashMap::new);

  public static BigDecimalByLocaleConstructor get() {
    return instance;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    String localeParameter = parameters.get("locale");
    if (localeParameter == null) {
      throw DslConfigurationException.BIG_DECIMAL_CONSTRUCTOR_REQUIRES_PARAMETER_LOCALE;
    }
    Locale locale = Locale.forLanguageTag(localeParameter);
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
