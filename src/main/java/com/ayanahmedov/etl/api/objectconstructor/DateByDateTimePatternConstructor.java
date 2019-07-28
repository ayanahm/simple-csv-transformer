package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.DateValueConstructor;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Map;

/**
 * Supports only Format Patterns:
 *
 * yyyy : {@link ChronoField#YEAR_OF_ERA}
 * MM   : {@link ChronoField#MONTH_OF_YEAR}
 * HH   : {@link ChronoField#HOUR_OF_DAY}
 * mm   : {@link ChronoField#MINUTE_OF_HOUR}
 * ss   : {@link ChronoField#SECOND_OF_MINUTE}
 *
 *
 * example:
 * <pre>
 *   yyyy-MM-dd:HH:mm:ss zz
 * </pre>
 *
 */
public class DateByDateTimePatternConstructor implements MappedCsvValueStringConstructor {
  private DateValueConstructor dateValueConstructor;

  public static DateByDateTimePatternConstructor of(DateValueConstructor constructor) {
    DateByDateTimePatternConstructor c = new DateByDateTimePatternConstructor();
    c.dateValueConstructor = constructor;
    return c;
  }

  @Override
  public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
    String sourceParsePattern = dateValueConstructor.getSourceFormatPattern();
    String formatPattern = dateValueConstructor.getTargetDateFormat().getFormatPattern();
    String zone = dateValueConstructor.getTargetDateFormat().getZoneId();
    ZoneId zoneId;
    if (zone == null) {
      zoneId = ZoneId.systemDefault();
    } else {
      zoneId= ZoneId.of(zone);
    }
    DateTimeFormatter sourceFmt;
    DateTimeFormatter targetFmt;
    try {

      sourceFmt = new DateTimeFormatterBuilder()
          .appendPattern(sourceParsePattern)
          .parseDefaulting(ChronoField.YEAR_OF_ERA, ZonedDateTime.now().getYear())
          .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
          .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
          .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
          .toFormatter()
          .withZone(zoneId);

      targetFmt = new DateTimeFormatterBuilder()
          .appendPattern(formatPattern)
          .toFormatter()
          .withZone(zoneId);

    } catch (IllegalArgumentException e) {
      throw DslConfigurationException.WRONG_DATE_TIME_DSL.withException(e);
    }

    try {
      Instant instantWithDefaultingZeroes = sourceFmt.parse(valueFromCsvMapping, Instant::from);
      String result = targetFmt.format(instantWithDefaultingZeroes);

      return CsvValueToJavaMappingResult.ofValue(result);
    } catch (DateTimeParseException dateTimeParseException) {
      return CsvValueToJavaMappingResult.ofMappingError(dateTimeParseException);
    }
  }
}
