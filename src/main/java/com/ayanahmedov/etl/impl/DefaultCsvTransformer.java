package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.CsvTransformationDslConfigProvider;
import com.ayanahmedov.etl.api.CsvTransformer;
import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.sourcemapper.LeadingAndTrailingWhiteSpaceTrimmer;
import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import com.ayanahmedov.etl.api.tostringformatter.BigDecimalByLocaleFormatter;
import com.ayanahmedov.etl.api.tostringformatter.ColumnAverageFormatter;
import com.ayanahmedov.etl.api.tostringformatter.FormattingToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of the CsvTransformer working with the backing configuration provider
 * as defined per {@link CsvTransformationDslConfigProvider}.
 */
public final class DefaultCsvTransformer implements CsvTransformer {
  private final static List<SourceCsvColumnMapper> builtInMappers = Arrays.asList(
      TwoDigitsNormalizer.get(),
      LeadingAndTrailingWhiteSpaceTrimmer.get()
  );

  private final static List<ReducedCsvValueToStringFormatter> builtInFormatters = Arrays.asList(
      BigDecimalByLocaleFormatter.getUninitialized(),
      ColumnAverageFormatter.get(),
      FormattingToStringFormatter.getUninitialized()
  );

  private final CsvTransformationConfig csvTransformationDslConfig;
  private final List<SourceCsvColumnMapper> mappers;
  private final List<ReducedCsvValueToStringFormatter> formatters;

  private DefaultCsvTransformer(CsvTransformationConfig config,
                                List<SourceCsvColumnMapper> mappers,
                                List<ReducedCsvValueToStringFormatter> formatters) {
    this.csvTransformationDslConfig = config;
    this.mappers = mappers;
    this.formatters = formatters;
  }

  public static DefaultCsvTransformer newTransformer(CsvTransformationConfig config,
                                                     List<SourceCsvColumnMapper> mappers,
                                                     List<ReducedCsvValueToStringFormatter> formatters) {

    List<ReducedCsvValueToStringFormatter> formattersWithBuiltIns = new ArrayList<>(formatters);
    formattersWithBuiltIns.addAll(builtInFormatters);

    List<SourceCsvColumnMapper> mappersWithBuiltIns = new ArrayList<>(mappers);
    mappersWithBuiltIns.addAll(builtInMappers);

    return new DefaultCsvTransformer(config, mappersWithBuiltIns, formattersWithBuiltIns);
  }


  @Override
  public void transform(Reader csvReader, Writer csvWriter) {
    transformAndWriteCsvInternal(csvReader, csvWriter);
  }

  private void transformAndWriteCsvInternal(Reader reader, Writer writer) {
    CSVReader csvReader = new CSVReader(reader);
    CSVWriter csvWriter = new CSVWriter(writer);

    LinkedHashMap<String, Integer> targetHeaderToIndex = calculateTargetHeaderFromDsl();
    String[] header = generateTargetHeaderRow(targetHeaderToIndex);
    LinkedHashMap<String, Integer> sourceHeaderToIndex = parseSourceHeader(csvReader);

    SourceHeaderRowAccessor headerRowAccessor = new SourceHeaderRowAccessorImpl(sourceHeaderToIndex);
    CsvRowMappingRuleCreator ruleCreator = new CsvRowMappingRuleCreator(headerRowAccessor, mappers, formatters);
    List<CsvRowMappingRule> rules = ruleCreator.createRules(csvTransformationDslConfig);
    CsvRowMapper mapper = new CsvRowMapper(rules, targetHeaderToIndex.size());

    //first write header
    csvWriter.writeNext(header);

    while (true) {
      try {
        String[] sourceRow = csvReader.readNext();
        if (null == sourceRow) {
          break;
        } else {
          String[] targetRow = mapper.mapRow(sourceRow);
          csvWriter.writeNext(targetRow);
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private LinkedHashMap<String, Integer> parseSourceHeader(CSVReader csvReader) {
    LinkedHashMap<String, Integer> sourceHeaderToIndex;
    try {
      String[] sourceHeaderRow = csvReader.readNext();
      if (null == sourceHeaderRow) {
        throw new RuntimeException("Could not get the first line(header) from the CSV file.");
      }
      sourceHeaderToIndex = parseSourceHeaderRow(sourceHeaderRow);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return sourceHeaderToIndex;
  }


  private String[] generateTargetHeaderRow(Map<String, Integer> targetHeaderToIndex) {
    String[] header = new String[targetHeaderToIndex.size()];
    Set<Map.Entry<String, Integer>> entrySet = targetHeaderToIndex.entrySet();

    int i = 0;
    for (Map.Entry<String, Integer> entry : entrySet) {
      header[i] = entry.getKey();
      i++;
    }
    return header;
  }

  private LinkedHashMap<String, Integer> calculateTargetHeaderFromDsl() {
    LinkedHashMap<String, Integer> res = new LinkedHashMap<>();

    List<String> targetColumns = csvTransformationDslConfig.getTransformation().stream()
        .map(SourceTransformation::getTargetSchemaColumn)
        .collect(Collectors.toList());

    for (int i = 0; i < targetColumns.size(); i++) {
      res.put(targetColumns.get(i).trim(), i);
    }

    return res;
  }

  private LinkedHashMap<String, Integer> parseSourceHeaderRow(String[] record) {
    LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
    for (int i = 0; i < record.length; i++) {
      res.put(record[i].trim(), i);
    }
    return res;
  }

  static class SourceHeaderRowAccessorImpl implements SourceHeaderRowAccessor {
    private LinkedHashMap<String, Integer> sourceHeaderToIndex;

    SourceHeaderRowAccessorImpl(LinkedHashMap<String, Integer> sourceHeaderToIndex) {
      this.sourceHeaderToIndex = sourceHeaderToIndex;
    }

    @Override
    public int getIndexByHeaderName(String header) {
      Integer index = sourceHeaderToIndex.get(header);
      if (null == index) {
        throw DslConfigurationException.UNKNOWN_SOURCE_HEADER
            .withAdditionalMessage("The header [" + header + "] is unknown.");
      }
      return index;
    }
  }
}
