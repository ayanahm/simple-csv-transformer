package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwoDigitsNormalizerTest {
  TwoDigitsNormalizer normalizer = TwoDigitsNormalizer.get();

  @ParameterizedTest
  @ValueSource(strings =
      {
          "5    ",
          "    5",
          "  5  ",
          "   05   ",
          "05  ",
          "   05",
          "05"
      })
  public void given_5_without_leading_null_then_return_05(String five) {
    String normalized = normalizer.map(five);
    assertEquals("05", normalized);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "12    ",
      "    12",
      "  12  ",
      "12"
  })
  public void given_12_then_return_12(String twelve) {
    String normalized = normalizer.map(twelve);
    assertEquals("12", normalized);
  }
}