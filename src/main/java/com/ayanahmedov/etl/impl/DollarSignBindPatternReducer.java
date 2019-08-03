package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.reducer.CsvValueReducer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DollarSignBindPatternReducer implements CsvValueReducer {
  private static final Pattern DOLLAR_SIGN_NUMBER_PATTERN = Pattern.compile("(\\$\\d)");
  private final String sourceBindPattern;
  private final List<Integer> expectedPlaceHolders;

  public DollarSignBindPatternReducer(String sourceBindPattern,
                                      List<Integer> expectedPlaceHolders) {
    this.sourceBindPattern = sourceBindPattern;
    this.expectedPlaceHolders = expectedPlaceHolders;
  }

  public static DollarSignBindPatternReducer get(String sourceBindPattern) {
    List<Integer> expectedPlaceHolders = new ArrayList<>();
    Matcher matcher = DOLLAR_SIGN_NUMBER_PATTERN.matcher(sourceBindPattern);
    while (matcher.find()) {
      String group = matcher.group(1);
      String[] placeHolder = group.split("\\$");
      String num = placeHolder[1];
      expectedPlaceHolders.add(Integer.parseInt(num));
    }
    return new DollarSignBindPatternReducer(sourceBindPattern, expectedPlaceHolders);
  }


  @Override
  public String reduce(List<String> mappedValues, List<Integer> positions) {
    if (!positions.containsAll(expectedPlaceHolders)) {

      throw DslConfigurationException.UNRESOLVED_BIND_PATTERN
          .withAdditionalMessage(
              "Not all the placeholders in the pattern are covered. " +
                  "Make sure to reference with $<num> correct mappings from the source. " +
                  "expected to have = [" + expectedPlaceHolders + "] " +
                  "but got [" + positions + "].");
    }

    String val = sourceBindPattern;
    for (int i = 0; i < positions.size(); i++) {
      Integer pos = positions.get(i);
      val = val.replace("$" + pos, mappedValues.get(i));
    }
    return val;
  }
}
