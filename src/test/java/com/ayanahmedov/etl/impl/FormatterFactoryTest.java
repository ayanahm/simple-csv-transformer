package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CustomFormatter;
import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FormatterFactoryTest {
  static class DummyFormatter implements MappedCsvValueToStringFormatter {

    @Override
    public MappedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
      return this;
    }

    @Override
    public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
      return CsvValueToJavaMappingResult.ofValue("");
    }
  }

  @Test
  public void create_custom_constructor() {
    TargetStringFormatter targetStringFormatter = new TargetStringFormatter()
        .withFormatterClass(new CustomFormatter()
            .withClassName(DummyFormatter.class.getCanonicalName()));

    DummyFormatter customConstructor = new DummyFormatter();
    MappedCsvValueToStringFormatter constructor = FormatterFactory.createFormatter(targetStringFormatter,
        Collections.singletonMap(customConstructor.getClass().getCanonicalName(), customConstructor));

    assertEquals(constructor, customConstructor);
  }

  @Test
  public void given_unknown_constructor_class_exception_thrown() {
    TargetStringFormatter targetStringFormatter = new TargetStringFormatter()
        .withFormatterClass(new CustomFormatter()
            .withClassName(String.class.getCanonicalName()));

    DummyFormatter customConstructor = new DummyFormatter();

    assertThrows(DslConfigurationException.class, () ->
        FormatterFactory.createFormatter(targetStringFormatter,
        Collections.singletonMap(customConstructor.getClass().getCanonicalName(), customConstructor)));
  }
}