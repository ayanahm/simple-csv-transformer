package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CustomConstructor;
import com.ayanahmedov.etl.api.dsl.ElementConstructor;
import com.ayanahmedov.etl.api.objectconstructor.MappedCsvValueStringConstructor;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectConstructorFactoryTest {
  static class DummyConstructor implements MappedCsvValueStringConstructor {

    @Override
    public CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters) {
      return null;
    }
  }

  @Test
  public void create_custom_constructor() {
    ElementConstructor elementConstructor = new ElementConstructor()
        .withConstructorClass(new CustomConstructor()
          .withClassName(DummyConstructor.class.getCanonicalName()));

    DummyConstructor customConstructor = new DummyConstructor();
    MappedCsvValueStringConstructor constructor = ObjectConstructorFactory.createObjectConstructor(elementConstructor,
        Collections.singletonMap(customConstructor.getClass().getCanonicalName(), customConstructor));

    assertEquals(constructor, customConstructor);
  }

  @Test
  public void given_unknown_constructor_class_exception_thrown() {
    ElementConstructor elementConstructor = new ElementConstructor()
        .withConstructorClass(new CustomConstructor()
            .withClassName(String.class.getCanonicalName()));

    DummyConstructor customConstructor = new DummyConstructor();

    assertThrows(DslConfigurationException.class, () -> ObjectConstructorFactory.createObjectConstructor(elementConstructor,
        Collections.singletonMap(customConstructor.getClass().getCanonicalName(), customConstructor)));
  }
}