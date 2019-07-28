package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.ElementConstructor;
import com.ayanahmedov.etl.api.objectconstructor.DateByDateTimePatternConstructor;
import com.ayanahmedov.etl.api.objectconstructor.IdenticalStringConstructor;
import com.ayanahmedov.etl.api.objectconstructor.MappedCsvValueStringConstructor;
import com.ayanahmedov.etl.api.objectconstructor.SimpleIntConstructor;

import java.util.Map;

public class ObjectConstructorFactory {
  public static MappedCsvValueStringConstructor createObjectConstructor(ElementConstructor elementConstructor,
                                                                        Map<String, MappedCsvValueStringConstructor> objectConstructorsByClassName) {
    if (elementConstructor.getDateValueConstructor() != null) {
      return DateByDateTimePatternConstructor.of(elementConstructor.getDateValueConstructor());
    } else if (elementConstructor.getStringValueConstructor() != null) {
      return IdenticalStringConstructor.of();
    } else if (elementConstructor.getIntValueConstructor() != null) {
      return SimpleIntConstructor.of();
    } else if (elementConstructor.getConstructorClass() != null) {
      String constructorClassName = elementConstructor.getConstructorClass().getClassName();
      MappedCsvValueStringConstructor customConstructor =
          objectConstructorsByClassName.get(constructorClassName);

      if (null == customConstructor) {
        throw new DslConfigurationException("Unknown custom Constructor provided. (" + constructorClassName +") " +
            "Make sure there is no type in configuration. " +
            "And the class is actually on classpath. " +
            "And provided to the transformer correctly.");
      }

      return customConstructor;
    } else {
      throw DslConfigurationException.UNKNOWN_ELEMENT_CONSTRUCTOR_IN_DSL;
    }
  }
}
