package com.ayanahmedov.etl.api.sourcemapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalizes to two digits by trimming white spaces and
 * and adding leading 0 if single digit.
 *
 * examples:
 * <pre>
 * 1 is normalized into 01
 * \s10\t is normalized into 10
 * </pre>
 */
public final class TwoDigitsNormalizer implements SourceValueMapper {
  private static final TwoDigitsNormalizer instance = new TwoDigitsNormalizer();

  private static final Pattern SINGLE_DIGIT_PATTERN = Pattern.compile("^(\\s*)(\\d)(\\s*)$");

  public static TwoDigitsNormalizer of() {
    return instance;
  }

  @Override
  public String map(String csvSourceValue) {
    Matcher matcher = SINGLE_DIGIT_PATTERN.matcher(csvSourceValue);
    if (matcher.find()) {
      return matcher.replaceAll("0$2");
    } else {
      return csvSourceValue.trim();
    }
  }

}
