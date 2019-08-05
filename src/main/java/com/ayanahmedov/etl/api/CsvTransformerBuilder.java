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

  /**
   * The dsl instance to configure the transformation rules.
   */
  public CsvTransformerBuilder withDsl(CsvTransformationConfig csvTransformationConfig) {
    this.dsl = csvTransformationConfig;
    return this;
  }

  /**
   * The XML instance of the transformations.
   * Which are parsed and validated against the xml schema.
   */
  public CsvTransformerBuilder withXmlDsl(Path pathToXmlDsl) {
    XmlDslConfigProvider p = new XmlDslConfigProvider(pathToXmlDsl);
    CsvTransformationConfig config = p.provide();
    return withDsl(config);
  }

  /**
   * Custom mappers referenced in the DSL instance.
   */
  public CsvTransformerBuilder withMappers(List<SourceCsvColumnMapper> mappers) {
    this.mappers.addAll(mappers);
    return this;
  }

  /**
   * Custom mapper referenced in the DSL instance.
   */
  public CsvTransformerBuilder withMapper(SourceCsvColumnMapper mapper) {
    mappers.add(mapper);
    return this;
  }

  /**
   * Custom formatters referenced in the DSL instance.
   */
  public CsvTransformerBuilder withFormatters(List<ReducedCsvValueToStringFormatter> formatters) {
    this.formatters.addAll(formatters);
    return this;
  }

  /**
   * Custom formatter referenced in the DSL instance.
   */
  public CsvTransformerBuilder withFormatter(ReducedCsvValueToStringFormatter formatter) {
    this.formatters.add(formatter);
    return this;
  }

  /**
   * Initializes a CsvTransformer.
   * If there are any custom mappers or formatters used
   * they should have been added before this method call.
   */
  public CsvTransformer build() {
    return DefaultCsvTransformer.newTransformer(
        dsl,
        mappers,
        formatters
        );
  }
}
