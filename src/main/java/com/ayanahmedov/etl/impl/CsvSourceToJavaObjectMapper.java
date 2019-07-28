package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.ConstructorParameter;
import com.ayanahmedov.etl.api.dsl.ElementConstructor;
import com.ayanahmedov.etl.api.dsl.SourceConstantValue;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.objectconstructor.MappedCsvValueStringConstructor;
import com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CsvSourceToJavaObjectMapper {

  private final SourceCsvValueAccessor sourceCsvValueAccessor;
  private final Map<String, MappedCsvValueStringConstructor> objectConstructorsByClassName;
  private final Map<String, SourceValueMapper> mappersByClassName;

  public CsvSourceToJavaObjectMapper(SourceCsvValueAccessor sourceCsvValueAccessor,
                                     List<SourceValueMapper> mappers,
                                     List<MappedCsvValueStringConstructor> constructors) {
    this.sourceCsvValueAccessor = sourceCsvValueAccessor;
    this.objectConstructorsByClassName = initConstructors(constructors);
    this.mappersByClassName = initMappers(mappers);
  }

  private Map<String, MappedCsvValueStringConstructor> initConstructors(List<MappedCsvValueStringConstructor> constructors) {
    Map<String, MappedCsvValueStringConstructor> result = new HashMap<>();
    if (constructors == null) {
      return result;
    }
    for (MappedCsvValueStringConstructor constructor : constructors) {
      result.put(constructor.getClass().getCanonicalName(), constructor);
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

  public CsvValueToJavaMappingResult map(List<SourceCsvColumn> sources, ElementConstructor elementConstructor) {
    String boundCsvValue = bindCsvValues(elementConstructor.getSourceBindPattern(), sources);
    return doMap(boundCsvValue, elementConstructor);
  }

  public CsvValueToJavaMappingResult map(SourceConstantValue constantValue, ElementConstructor elementConstructor) {
    String boundCsvValue = constantValue.getValue();
    return doMap(boundCsvValue, elementConstructor);
  }

  private CsvValueToJavaMappingResult doMap(String param, ElementConstructor elementConstructor) {
    MappedCsvValueStringConstructor objectConstructor = ObjectConstructorFactory.createObjectConstructor(elementConstructor, objectConstructorsByClassName);

    Map<String, String> customConstructorParams = Collections.emptyMap();
    if (elementConstructor.getConstructorClass() != null) {
      customConstructorParams = elementConstructor.getConstructorClass().getParameter().stream()
          .collect(Collectors.toMap(
              ConstructorParameter::getName,
              ConstructorParameter::getValue
          ));
    }
    CsvValueToJavaMappingResult object = objectConstructor.constructString(param, customConstructorParams);
    if (object == null) {
      throw new DslConfigurationException("Object constructor returned null. This is a wrong implementation." + objectConstructor.getClass().getCanonicalName());
    }
    return object;
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
