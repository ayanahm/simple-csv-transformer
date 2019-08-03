package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.sourcemapper.SourceColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvRowMappingRuleCreator {
  private final SourceHeaderRowAccessor sourceHeaderRowAccessor;
  private final List<SourceColumnMapper> mappers;
  private final List<MappedCsvValueToStringFormatter> formatters;

  public CsvRowMappingRuleCreator(SourceHeaderRowAccessor sourceHeaderRowAccessor,
                                  List<SourceColumnMapper> mappers,
                                  List<MappedCsvValueToStringFormatter> formatters) {
    this.sourceHeaderRowAccessor = sourceHeaderRowAccessor;
    this.mappers = mappers;
    this.formatters = formatters;
  }

  public List<CsvRowMappingRule> createRules(CsvTransformationConfig config) {
    return config.getTransformation().stream()
        .map(this::createRule)
        .collect(Collectors.toList());
  }

  static final class MappingRuleImpl implements CsvRowMappingRule {

    private final List<Integer> sourceColumnIndexes;
    private final Map<Integer, SourceColumnMapper> mappersByIndex;
    private final MappedCsvValueToStringFormatter formatter;
    private final CsvValueReducer reducer;
    private List<Integer> sourceColumnConstructorPositions;

    public MappingRuleImpl(List<Integer> sourceColumnIndexes,
                           Map<Integer, SourceColumnMapper> mappersByIndex,
                           MappedCsvValueToStringFormatter formatter,
                           CsvValueReducer reducer,
                           List<Integer> bindPatternPositions) {
      this.sourceColumnIndexes = sourceColumnIndexes;
      this.mappersByIndex = mappersByIndex;
      this.formatter = formatter;
      this.reducer = reducer;
      this.sourceColumnConstructorPositions = bindPatternPositions;
    }

    @Override
    public List<Integer> getSourceColumnIndexes() {
      return sourceColumnIndexes;
    }

    @Override
    public List<Integer> getBindPatternPositions() {
      return sourceColumnConstructorPositions;
    }

    @Override
    public Map<Integer, SourceColumnMapper> getMappersByIndex() {
      return mappersByIndex;
    }

    @Override
    public CsvValueReducer getReducer() {
      return reducer;
    }

    @Override
    public MappedCsvValueToStringFormatter getFormatter() {
      return formatter;
    }
  }

  private CsvRowMappingRule createRule(SourceTransformation transformation) {
    Map<Integer, SourceColumnMapper> sourceMappers = getSourceMappers(transformation);
    List<Integer> sourceColumnIndexes = transformation.getSourceColumn().stream()
        .map(col -> sourceHeaderRowAccessor.getIndexByHeaderName(col.getName()))
        .collect(Collectors.toList());

    String dollarSignBindPattern = transformation.getSourceBindPattern();
    DollarSignBindPatternReducer reducer = DollarSignBindPatternReducer.get(dollarSignBindPattern);

    MappedCsvValueToStringFormatter formatter = FormatterFactory.createFormatter(transformation.getTargetStringFormatter(), formatters);

    List<Integer> bindPatternPositions = transformation.getSourceColumn().stream()
        .map(SourceCsvColumn::getBindPatternPosition)
        .collect(Collectors.toList());

    return new MappingRuleImpl(
        sourceColumnIndexes,
        sourceMappers,
        formatter,
        reducer,
        bindPatternPositions);
  }


  private Map<Integer, SourceColumnMapper> getSourceMappers(SourceTransformation transform) {
    Map<Integer, SourceColumnMapper> r = new HashMap<>();
    for (SourceCsvColumn sourceCsvColumn : transform.getSourceColumn()) {
      String mapperClass = sourceCsvColumn.getSourceValueMapper();
      if (null != mapperClass) {
        SourceColumnMapper mapper = mappers.stream()
            .filter(m -> m.getClass().getCanonicalName().equals(mapperClass))
            .findFirst()
            .orElseThrow(() ->
                DslConfigurationException.UNKNOWN_MAPPER_IN_DSL
                    .withAdditionalMessage("Could not find class:" + mapperClass));
        r.put(sourceHeaderRowAccessor.getIndexByHeaderName(sourceCsvColumn.getName()), mapper);
      }
    }

    return r;
  }
}
