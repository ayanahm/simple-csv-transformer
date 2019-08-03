package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.DslConfigurationException;
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
public class DateByDateTimePatternFormatter implements MappedCsvValueToStringFormatter {
  public static final String PARAM_SOURCE_PARSE_PATTERN = "source-parse-pattern";
  public static final String PARAM_TARGET_FORMAT_PATTER = "target-format-pattern";
  public static final String PARAM_TARGET_ZONE_ID = "target-zone-id";

  private DateTimeFormatter sourceFmt;
  private DateTimeFormatter targetFmt;

  private DateByDateTimePatternFormatter() {
  }

  @Override
  public void init(Map<String, String> parameters) {
    //do nothing since initialized by of(parameters) method
  }

  public static DateByDateTimePatternFormatter of(Map<String, String> parameters) {
    DateByDateTimePatternFormatter c = new DateByDateTimePatternFormatter();

    String sourceParsePattern = parameters.get(PARAM_SOURCE_PARSE_PATTERN);
    String formatPattern = parameters.get(PARAM_TARGET_FORMAT_PATTER);
    String zone = parameters.get(PARAM_TARGET_ZONE_ID);
    ZoneId zoneId;
    if (zone == null) {
      zoneId = ZoneId.systemDefault();
    } else {
      zoneId= ZoneId.of(zone);
    }

    try {

      c.sourceFmt = new DateTimeFormatterBuilder()
          .appendPattern(sourceParsePattern)
          .parseDefaulting(ChronoField.YEAR_OF_ERA, ZonedDateTime.now().getYear())
          .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1L)
          .parseDefaulting(ChronoField.DAY_OF_MONTH, 1L)
          .parseDefaulting(ChronoField.HOUR_OF_DAY, 0L)
          .toFormatter()
          .withZone(zoneId);

      c.targetFmt = new DateTimeFormatterBuilder()
          .appendPattern(formatPattern)
          .toFormatter()
          .withZone(zoneId);

    } catch (IllegalArgumentException e) {
      throw DslConfigurationException.WRONG_DATE_TIME_DSL.withException(e);
    }

    return c;
  }


  @Override
  public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
    try {
      Instant instantWithDefaultingZeroes = sourceFmt.parse(valueFromCsvMapping, Instant::from);
      String result = targetFmt.format(instantWithDefaultingZeroes);

      return CsvValueToJavaMappingResult.ofValue(result);
    } catch (DateTimeParseException dateTimeParseException) {
      return CsvValueToJavaMappingResult.ofMappingError(dateTimeParseException);
    }
  }
}
