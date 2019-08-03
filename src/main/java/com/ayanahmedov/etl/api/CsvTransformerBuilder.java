package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;
import com.ayanahmedov.etl.impl.DefaultCsvTransformer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvTransformerBuilder {
  private CsvTransformationConfig dsl;
  private List<SourceCsvColumnMapper> mappers = new ArrayList<>();
  private List<ReducedCsvValueToStringFormatter> formatters = new ArrayList<>();

  public static CsvTransformerBuilder builder() {
    return new CsvTransformerBuilder();
  }

  public CsvTransformerBuilder withDsl(CsvTransformationConfig csvTransformationConfig) {
    this.dsl = csvTransformationConfig;
    return this;
  }

  public CsvTransformerBuilder withXmlDsl(Path pathToXmlDsl) {
    XmlDslConfigProvider p = new XmlDslConfigProvider(pathToXmlDsl);
    CsvTransformationConfig config = p.provide();
    return withDsl(config);
  }


  public CsvTransformerBuilder withMappers(List<SourceCsvColumnMapper> mappers) {
    this.mappers.addAll(mappers);
    return this;
  }

  public CsvTransformerBuilder withMapper(SourceCsvColumnMapper mapper) {
    mappers.add(mapper);
    return this;
  }

  public CsvTransformerBuilder withFormatters(List<ReducedCsvValueToStringFormatter> formatters) {
    this.formatters.addAll(formatters);
    return this;
  }

  public CsvTransformerBuilder withFormatter(ReducedCsvValueToStringFormatter formatter) {
    this.formatters.add(formatter);
    return this;
  }

  public CsvTransformer build() {
    return DefaultCsvTransformer.newTransformer(
        dsl,
        mappers,
        formatters
        );
  }
}
