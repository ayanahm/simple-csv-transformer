package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.DateByDateTimePatternFormatter;
import com.ayanahmedov.etl.api.tostringformatter.IdenticalToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.SimpleIntFormatter;

import java.util.Map;

public class TargetStringFormatterFactory {
  public static MappedCsvValueToStringFormatter createFormatter(TargetStringFormatter targetStringFormatter,
                                                                Map<String, MappedCsvValueToStringFormatter> formattersByClassName) {
    if (targetStringFormatter.getDateValueFormatter() != null) {
      return DateByDateTimePatternFormatter.of(targetStringFormatter.getDateValueFormatter());
    } else if (targetStringFormatter.getStringValueFormatter() != null) {
      return IdenticalToStringFormatter.of();
    } else if (targetStringFormatter.getIntValueFormatter() != null) {
      return SimpleIntFormatter.of();
    } else if (targetStringFormatter.getFormatterClass() != null) {
      String constructorClassName = targetStringFormatter.getFormatterClass().getClassName();
      MappedCsvValueToStringFormatter customFormatter =
          formattersByClassName.get(constructorClassName);

      if (null == customFormatter) {
        throw new DslConfigurationException("Unknown custom Constructor provided. (" + constructorClassName + ") " +
            "Make sure there is no type in configuration. " +
            "And the class is actually on classpath. " +
            "And provided to the transformer correctly.");
      }

      return customFormatter;
    } else {
      throw DslConfigurationException.UNKNOWN_ELEMENT_CONSTRUCTOR_IN_DSL;
    }
  }
}
