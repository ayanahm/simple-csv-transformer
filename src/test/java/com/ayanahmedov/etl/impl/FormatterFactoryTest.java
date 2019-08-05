package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CustomFormatter;
import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FormatterFactoryTest {
  static class DummyFormatter implements ReducedCsvValueToStringFormatter {

    @Override
    public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
      return this;
    }

    @Override
    public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
      return CsvValueToJavaMappingResult.ofValue("");
    }
  }

  @Test
  public void create_custom_formatter() {
    TargetStringFormatter targetStringFormatter = new TargetStringFormatter()
        .withFormatterClass(new CustomFormatter()
            .withClassName(DummyFormatter.class.getName()));

    DummyFormatter dummy = new DummyFormatter();
    ReducedCsvValueToStringFormatter formatter = FormatterFactory.createFormatter(targetStringFormatter,
        Collections.singletonMap(dummy.getClass().getName(), dummy));

    assertEquals(formatter, dummy);
  }

  @Test
  public void given_unknown_formatter_class_exception_thrown() {
    TargetStringFormatter targetStringFormatter = new TargetStringFormatter()
        .withFormatterClass(new CustomFormatter()
            .withClassName(String.class.getName()));

    DummyFormatter dummy = new DummyFormatter();

    assertThrows(DslConfigurationException.class, () ->
        FormatterFactory.createFormatter(targetStringFormatter,
        Collections.singletonMap(dummy.getClass().getName(), dummy)));
  }
}