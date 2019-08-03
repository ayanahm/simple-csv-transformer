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
  private List<SourceCsvColumnMapper> sourceCsvMappers = new ArrayList<>();
  private List<ReducedCsvValueToStringFormatter> constructors = new ArrayList<>();

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


  public CsvTransformerBuilder withSourceCsvValueMappers(List<SourceCsvColumnMapper> mappers) {
    this.sourceCsvMappers.addAll(mappers);
    return this;
  }

  public CsvTransformerBuilder withSourceCsvValueMapper(SourceCsvColumnMapper mapper) {
    sourceCsvMappers.add(mapper);
    return this;
  }

  public CsvTransformerBuilder withObjectConstructors(List<ReducedCsvValueToStringFormatter> constructors) {
    this.constructors.addAll(constructors);
    return this;
  }

  public CsvTransformerBuilder withObjectConstructor(ReducedCsvValueToStringFormatter constructor) {
    this.constructors.add(constructor);
    return this;
  }


  public CsvTransformer build() {
    return DefaultCsvTransformer.newTransformer(
        dsl,
        sourceCsvMappers,
        constructors
        );
  }
}
