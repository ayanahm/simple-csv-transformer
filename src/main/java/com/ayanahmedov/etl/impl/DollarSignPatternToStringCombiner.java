package com.ayanahmedov.etl.impl;

public class DollarSignPatternToStringCombiner {

  /**
   * Combines source csv columns for the currently scanned row,
   * into a single string by the provided pattern.
   *
   * Example pattern:
   * `$1,$2`
   * the columns with
   * constructorPosition=1 and constructorPosition=2
   * will be replaced in the pattern.
   * And result in
   * `100,200`
   * given the source CSV record contains 100 and 200 respectively.
   *
   */
  public static String combineByPattern(String sourceBindPattern, String[] mappedValues, int[] positions) {
    for (int i = 0; i < positions.length; i++) {
      sourceBindPattern = sourceBindPattern.replace("$" + positions[i], mappedValues[i]);
    }
    return sourceBindPattern;
  }
}
