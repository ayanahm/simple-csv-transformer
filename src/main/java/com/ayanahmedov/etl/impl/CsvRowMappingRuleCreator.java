package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvRowMappingRuleCreator {
  private final SourceHeaderRowAccessor sourceHeaderRowAccessor;
  private final List<SourceCsvColumnMapper> mappers;
  private final List<ReducedCsvValueToStringFormatter> formatters;

  public CsvRowMappingRuleCreator(SourceHeaderRowAccessor sourceHeaderRowAccessor,
                                  List<SourceCsvColumnMapper> mappers,
                                  List<ReducedCsvValueToStringFormatter> formatters) {
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
    private final Map<Integer, SourceCsvColumnMapper> mappersByIndex;
    private final ReducedCsvValueToStringFormatter formatter;
    private final CsvValueReducer reducer;
    private List<Integer> bindPatternPositions;

    public MappingRuleImpl(List<Integer> sourceColumnIndexes,
                           Map<Integer, SourceCsvColumnMapper> mappersByIndex,
                           ReducedCsvValueToStringFormatter formatter,
                           CsvValueReducer reducer,
                           List<Integer> bindPatternPositions) {
      this.sourceColumnIndexes = sourceColumnIndexes;
      this.mappersByIndex = mappersByIndex;
      this.formatter = formatter;
      this.reducer = reducer;
      this.bindPatternPositions = bindPatternPositions;
    }

    @Override
    public List<Integer> getSourceColumnIndexes() {
      return sourceColumnIndexes;
    }

    @Override
    public List<Integer> getBindPatternPositions() {
      return bindPatternPositions;
    }

    @Override
    public Map<Integer, SourceCsvColumnMapper> getMappersByIndex() {
      return mappersByIndex;
    }

    @Override
    public CsvValueReducer getReducer() {
      return reducer;
    }

    @Override
    public ReducedCsvValueToStringFormatter getFormatter() {
      return formatter;
    }
  }

  private CsvRowMappingRule createRule(SourceTransformation transformation) {
    Map<Integer, SourceCsvColumnMapper> sourceMappers = getSourceMappers(transformation);
    List<Integer> sourceColumnIndexes = transformation.getSourceColumn().stream()
        .map(col -> sourceHeaderRowAccessor.getIndexByHeaderName(col.getName()))
        .collect(Collectors.toList());

    String dollarSignBindPattern = transformation.getSourceBindPattern();
    DollarSignBindPatternReducer reducer = DollarSignBindPatternReducer.get(dollarSignBindPattern);

    ReducedCsvValueToStringFormatter formatter = FormatterFactory.createFormatter(transformation.getTargetStringFormatter(), formatters);

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


  private Map<Integer, SourceCsvColumnMapper> getSourceMappers(SourceTransformation transform) {
    Map<Integer, SourceCsvColumnMapper> r = new HashMap<>();
    for (SourceCsvColumn sourceCsvColumn : transform.getSourceColumn()) {
      String mapperClass = sourceCsvColumn.getSourceValueMapper();
      if (null != mapperClass) {
        SourceCsvColumnMapper mapper = mappers.stream()
            .filter(m -> m.getClass().getName().equals(mapperClass))
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
