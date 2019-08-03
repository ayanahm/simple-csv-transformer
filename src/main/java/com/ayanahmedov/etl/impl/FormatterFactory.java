package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.DateValueFormatter;
import com.ayanahmedov.etl.api.dsl.FormatterParameter;
import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.DateByDateTimePatternFormatter;
import com.ayanahmedov.etl.api.tostringformatter.IdenticalToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.SimpleIntFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormatterFactory {
  public static MappedCsvValueToStringFormatter createFormatter(TargetStringFormatter targetStringFormatter,
                                                                List<MappedCsvValueToStringFormatter> formatters) {
    return createFormatter(targetStringFormatter, createFormattersByClassName(formatters));
  }

  public static MappedCsvValueToStringFormatter createFormatter(TargetStringFormatter targetStringFormatter,
                                                                Map<String, MappedCsvValueToStringFormatter> formattersByClassName) {
    if (targetStringFormatter.getDateValueFormatter() != null) {
      DateValueFormatter dateFormatter = targetStringFormatter.getDateValueFormatter();

      Map<String, String> params = new HashMap<>();
      params.put(DateByDateTimePatternFormatter.PARAM_SOURCE_PARSE_PATTERN, dateFormatter.getSourceFormatPattern());
      params.put(DateByDateTimePatternFormatter.PARAM_TARGET_FORMAT_PATTER, dateFormatter.getTargetDateFormat().getFormatPattern());
      params.put(DateByDateTimePatternFormatter.PARAM_TARGET_ZONE_ID, dateFormatter.getTargetDateFormat().getZoneId());

      DateByDateTimePatternFormatter formatter = DateByDateTimePatternFormatter.getUninitialized();
      return formatter.newInstance(params);

    } else if (targetStringFormatter.getStringValueFormatter() != null) {
      return IdenticalToStringFormatter.get();
    } else if (targetStringFormatter.getIntValueFormatter() != null) {
      return SimpleIntFormatter.get();
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

      if (targetStringFormatter.getFormatterClass() != null) {
        Map<String, String> customFormatterParameters = targetStringFormatter.getFormatterClass().getParameter().stream()
            .collect(Collectors.toMap(
                FormatterParameter::getName,
                FormatterParameter::getValue
            ));

        // This is done for thread-safety.
        // if the existing instance used, then there is no guarantee for thread-safety
        // since the parameters saved in the instances may conflict across different DSL instances.
        customFormatter = customFormatter.newInstance(customFormatterParameters);
      }

      return customFormatter;
    } else {
      throw DslConfigurationException.UNKNOWN_FORMATTER_IN_DSL;
    }
  }

  private static Map<String, MappedCsvValueToStringFormatter> createFormattersByClassName(List<MappedCsvValueToStringFormatter> constructors) {
    Map<String, MappedCsvValueToStringFormatter> result = new HashMap<>();
    if (constructors == null) {
      return result;
    }
    for (MappedCsvValueToStringFormatter formatter : constructors) {
      result.put(formatter.getClass().getCanonicalName(), formatter);
    }
    return result;
  }
}
