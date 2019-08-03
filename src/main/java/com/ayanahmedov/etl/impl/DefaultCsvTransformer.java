package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.CsvTransformationDslConfigProvider;
import com.ayanahmedov.etl.api.CsvTransformer;
import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.sourcemapper.LeadingAndTrailingWhiteSpaceTrimmer;
import com.ayanahmedov.etl.api.sourcemapper.SourceColumnMapper;
import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import com.ayanahmedov.etl.api.tostringformatter.BigDecimalByLocaleFormatter;
import com.ayanahmedov.etl.api.tostringformatter.ColumnAverageFormatter;
import com.ayanahmedov.etl.api.tostringformatter.FormattingToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of the CsvTransformer working with the backing configuration provider
 * as defined per {@link CsvTransformationDslConfigProvider}.
 */
public final class DefaultCsvTransformer implements CsvTransformer {
  private final static List<SourceColumnMapper> builtInMappers = Arrays.asList(
      TwoDigitsNormalizer.get(),
      LeadingAndTrailingWhiteSpaceTrimmer.get()
  );

  private final static List<MappedCsvValueToStringFormatter> builtInFormatters = Arrays.asList(
      BigDecimalByLocaleFormatter.getUninitialized(),
      ColumnAverageFormatter.get(),
      FormattingToStringFormatter.getUninitialized()
  );

  private final CsvTransformationConfig csvTransformationDslConfig;
  private final List<SourceColumnMapper> mappers;
  private final List<MappedCsvValueToStringFormatter> formatters;

  private DefaultCsvTransformer(CsvTransformationConfig config,
                                List<SourceColumnMapper> mappers,
                                List<MappedCsvValueToStringFormatter> formatters) {
    this.csvTransformationDslConfig = config;
    this.mappers = mappers;
    this.formatters = formatters;
  }

  public static DefaultCsvTransformer newTransformer(CsvTransformationConfig config,
                                                     List<SourceColumnMapper> mappers,
                                                     List<MappedCsvValueToStringFormatter> constructors) {

    List<MappedCsvValueToStringFormatter> constructorsWithBuiltIns = new ArrayList<>(constructors);
    constructorsWithBuiltIns.addAll(builtInFormatters);

    List<SourceColumnMapper> mappersWithBuiltIns = new ArrayList<>(mappers);
    mappersWithBuiltIns.addAll(builtInMappers);

    return new DefaultCsvTransformer(config, mappersWithBuiltIns, constructorsWithBuiltIns);
  }


  @Override
  public void transform(Reader csvReader, Writer csvWriter) {
    transformAndWriteCsvInternal(csvReader, csvWriter);
  }

  private void transformAndWriteCsvInternal(Reader reader, Writer writer) {
    CSVReader csvReader = new CSVReader(reader);
    CSVWriter csvWriter = new CSVWriter(writer);

    Map<String, Integer> sourceHeaderToIndex;
    Map<String, Integer> targetHeaderToIndex = calculateTargetHeaderFromDsl();

    try {
      String[] sourceHeaderRow = csvReader.readNext();
      if (null == sourceHeaderRow) {
        throw new RuntimeException("Could not get the first line(header) from the CSV file.");
      }
      sourceHeaderToIndex = parseSourceHeaderRow(sourceHeaderRow);
      String[] header = generateTargetHeaderRow(targetHeaderToIndex);
      csvWriter.writeNext(header);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    SourceHeaderRowAccessor headerRowAccessor = new SourceHeaderRowAccessorImpl(sourceHeaderToIndex);
    CsvRowMappingRuleCreator ruleCreator = new CsvRowMappingRuleCreator(headerRowAccessor, mappers, formatters);
    List<CsvRowMappingRule> rules = ruleCreator.createRules(csvTransformationDslConfig);
    CsvRecordMapper mapper = new CsvRecordMapper(rules, targetHeaderToIndex.size());

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

  private Map<String, Integer> calculateTargetHeaderFromDsl() {
    Map<String, Integer> res = new HashMap<>();

    List<String> targetColumns = csvTransformationDslConfig.getTransformation().stream()
        .map(SourceTransformation::getTargetSchemaColumn)
        .collect(Collectors.toList());

    for (int i = 0; i < targetColumns.size(); i++) {
      res.put(targetColumns.get(i).trim(), i);
    }

    return res;
  }

  private Map<String, Integer> parseSourceHeaderRow(String[] record) {
    Map<String, Integer> res = new HashMap<>();
    for (int i = 0; i < record.length; i++) {
      res.put(record[i].trim(), i);
    }
    return res;
  }

  static class SourceHeaderRowAccessorImpl implements SourceHeaderRowAccessor {
    private Map<String, Integer> sourceHeaderToIndex;

    SourceHeaderRowAccessorImpl(Map<String, Integer> sourceHeaderToIndex) {
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
