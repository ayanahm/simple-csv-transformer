package com.ayanahmedov.etl.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DollarSignPatternToStringCombinerTest {

  @ParameterizedTest
  @CsvSource({
      "$1,        a| ,   a",
      "$1-$2,     a|b,   a-b",
      "$2,        a|b,   b",
      "$1,         | ,    ",
      "$1$2:sfx,   | ,   :sfx",
  }
  )
  public void test_combiner(String pattern,
                            String pipeSeparatedSources,
                            String expected) {

    String[] sourceValues = pipeSeparatedSources.split("\\|", -1);
    expected = expected == null ? "" : expected;

    CsvValueReducer dollarSignBindPatternReducer = DollarSignBindPatternReducer.get(pattern);
    String out = dollarSignBindPatternReducer.reduce(Arrays.asList(sourceValues), Arrays.asList(1, 2));


    assertEquals(expected, out);
  }
}