package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.FormatterParameter;
import com.ayanahmedov.etl.api.dsl.SourceConstantValue;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CsvSourceToJavaObjectMapper {

  private final SourceCsvValueAccessor sourceCsvValueAccessor;
  private final Map<String, MappedCsvValueToStringFormatter> formattersByClassName;
  private final Map<String, SourceValueMapper> mappersByClassName;

  CsvSourceToJavaObjectMapper(SourceCsvValueAccessor sourceCsvValueAccessor,
                              List<SourceValueMapper> mappers,
                              List<MappedCsvValueToStringFormatter> formatters) {
    this.sourceCsvValueAccessor = sourceCsvValueAccessor;
    this.formattersByClassName = initFormatters(formatters);
    this.mappersByClassName = initMappers(mappers);
  }

  private Map<String, MappedCsvValueToStringFormatter> initFormatters(List<MappedCsvValueToStringFormatter> constructors) {
    Map<String, MappedCsvValueToStringFormatter> result = new HashMap<>();
    if (constructors == null) {
      return result;
    }
    for (MappedCsvValueToStringFormatter formatter : constructors) {
      result.put(formatter.getClass().getCanonicalName(), formatter);
    }
    return result;
  }

  private Map<String, SourceValueMapper> initMappers(List<SourceValueMapper> mappers) {
    Map<String, SourceValueMapper> result = new HashMap<>();
    if (mappers == null) {
      return result;
    }
    for (SourceValueMapper mapper : mappers) {
      result.put(mapper.getClass().getCanonicalName(), mapper);
    }
    return result;
  }

  public CsvValueToJavaMappingResult map(List<SourceCsvColumn> sources, TargetStringFormatter targetStringFormatter) {
    String boundCsvValue = bindCsvValues(targetStringFormatter.getSourceBindPattern(), sources);
    return doMap(boundCsvValue, targetStringFormatter);
  }

  public CsvValueToJavaMappingResult map(SourceConstantValue constantValue, TargetStringFormatter targetStringFormatter) {
    String boundCsvValue = constantValue.getValue();
    return doMap(boundCsvValue, targetStringFormatter);
  }

  private CsvValueToJavaMappingResult doMap(String param, TargetStringFormatter targetStringFormatter) {
    MappedCsvValueToStringFormatter formatter = TargetStringFormatterFactory.createFormatter(targetStringFormatter, formattersByClassName);

    Map<String, String> customFormatterParameters = Collections.emptyMap();
    if (targetStringFormatter.getFormatterClass() != null) {
      customFormatterParameters = targetStringFormatter.getFormatterClass().getParameter().stream()
          .collect(Collectors.toMap(
              FormatterParameter::getName,
              FormatterParameter::getValue
          ));
    }
    CsvValueToJavaMappingResult result = formatter.formatToString(param, customFormatterParameters);
    if (result == null) {
      throw new DslConfigurationException("Object constructor returned null. This is a wrong implementation." + formatter.getClass().getCanonicalName());
    }
    return result;
  }

  /**
   * Creates a parameter pattern based on the configured mappings from @param sourceCsvColumn.
   * In case sourceBindPattern is null, first sourceCsv column mapping, pointing to the value indexed by header is returned.
   * If there is no sourceCsvColumn present, null is returned.
   * <p>
   * In case sourceBindPattern is null, but there are more than 1 @param sourceCsvColumn is provided,
   * then {@link IllegalStateException} is thrown. Since this is a configuration problem, and needs to be fixed on configuration level.
   */
  private String bindCsvValues(String sourceBindPattern, List<SourceCsvColumn> sourceCsvColumns) {
    String[] mappedValues = new String[sourceCsvColumns.size()];
    int[] positions = new int[sourceCsvColumns.size()];

    for (int i = 0; i < sourceCsvColumns.size(); i++) {
      SourceCsvColumn sourceCsvColumn = sourceCsvColumns.get(i);

      String colValue = sourceCsvValueAccessor.getValueForHeader(sourceCsvColumn.getName());
      if (sourceCsvColumn.getSourceValueMapper() != null) {
        SourceValueMapper mapper = getMapper(sourceCsvColumn.getSourceValueMapper());
        colValue = mapper.map(colValue);
      }
      int pos = sourceCsvColumn.getConstructorPosition();
      mappedValues[i] = colValue;
      positions[i] = pos;
    }
    return DollarSignPatternToStringCombiner.combineByPattern(sourceBindPattern, mappedValues, positions);
  }

  private SourceValueMapper getMapper(String canonicalClassName) {
    SourceValueMapper sourceValueMapper = mappersByClassName.get(canonicalClassName);
    if (sourceValueMapper == null) {
      throw new DslConfigurationException(
          "Cannot find the mapper asked for. " +
              "Maybe forgotten to pass it during the initialization? ");
    }

    return sourceValueMapper;
  }

}
