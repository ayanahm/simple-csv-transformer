package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.CsvTransformationDslConfigProvider;
import com.ayanahmedov.etl.api.CsvTransformer;
import com.ayanahmedov.etl.api.DslConfigurationException;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Default implementation of the CsvTransformer working with the backing configuration provider
 * as defined per {@link CsvTransformationDslConfigProvider}.
 */
public final class DefaultCsvTransformer implements CsvTransformer {
  private final static Logger log = Logger.getLogger(DefaultCsvTransformer.class.getName());

  private final static List<SourceValueMapper> builtInMappers = Arrays.asList(
      TwoDigitsNormalizer.get()
  );

  private final static List<MappedCsvValueToStringFormatter> builtInFormatters = Arrays.asList(
      BigDecimalByLocaleFormatter.get(),
      ColumnAverageFormatter.get(),
      FormattingToStringFormatter.get()
  );

  private final CsvTransformationConfig csvTransformationDslConfig;
  private final List<SourceValueMapper> mappers;
  private final List<MappedCsvValueToStringFormatter> formatters;

  private DefaultCsvTransformer(CsvTransformationConfig config,
                                List<SourceValueMapper> mappers,
                                List<MappedCsvValueToStringFormatter> formatters) {
    this.csvTransformationDslConfig = config;
    this.mappers = mappers;
    this.formatters = formatters;
  }

  public static DefaultCsvTransformer newTransformer(CsvTransformationConfig config,
                                                     List<SourceValueMapper> mappers,
                                                     List<MappedCsvValueToStringFormatter> constructors) {

    List<MappedCsvValueToStringFormatter> constructorsWithBuiltIns = new ArrayList<>(constructors);
    constructorsWithBuiltIns.addAll(builtInFormatters);

    List<SourceValueMapper> mappersWithBuiltIns = new ArrayList<>(mappers);
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

    while (true) {
      try {
        String[] sourceRow = csvReader.readNext();
        if (null == sourceRow) {
          break;
        } else {
          SourceCsvValueAccessor sourceCsvValueAccessor = new SourceValueAccessByMap(sourceHeaderToIndex, sourceRow);
          String[] newRow = mapRow(sourceCsvValueAccessor, targetHeaderToIndex);
          csvWriter.writeNext(newRow);
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

  private String[] mapRow(SourceCsvValueAccessor sourceCsvValueAccessor, Map<String, Integer> targetHeaderToIndex) {
    String[] newRow = new String[targetHeaderToIndex.size()];
    CsvSourceToJavaObjectMapper csvSourceToJavaObjectMapper = new CsvSourceToJavaObjectMapper(sourceCsvValueAccessor, mappers, formatters);
    List<SourceTransformation> transformations = csvTransformationDslConfig.getTransformation();

    for (SourceTransformation sourceTransformation : transformations) {
      if (sourceTransformation.getSourceColumn().isEmpty() && sourceTransformation.getConstantSource() == null) {
        throw new DslConfigurationException(
            "Either sourceColumn must be non-empty or constantSource must be present.");
      }

      CsvValueToJavaMappingResult mappingResult;
      if (!sourceTransformation.getSourceColumn().isEmpty()) {
        mappingResult = csvSourceToJavaObjectMapper.map(sourceTransformation.getSourceColumn(), sourceTransformation.getTargetStringFormatter());
      } else {
        mappingResult = csvSourceToJavaObjectMapper.map(sourceTransformation.getConstantSource(), sourceTransformation.getTargetStringFormatter());
      }
      final int index = targetHeaderToIndex.get(sourceTransformation.getTargetSchemaColumn());
      mappingResult.getValue().ifPresent(mapped -> newRow[index] = mapped);

      mappingResult.getException().ifPresent(exp -> {
        newRow[index] = "";
        log.log(Level.WARNING,
            "the transformation could not be applied on SourceColumns:[{0}]. With exception {1}",
            new Object[]{
                sourceTransformation.getSourceColumn().stream().map(SourceCsvColumn::getName).collect(Collectors.joining(",")),
                exp
            });
      });
    }

    return newRow;
  }

  static class SourceValueAccessByMap implements SourceCsvValueAccessor {
    private final Map<String, Integer> headerToIndex;
    private final String[] record;

    SourceValueAccessByMap(Map<String, Integer> headerToIndex, String[] record) {
      this.headerToIndex = headerToIndex;
      this.record = record;
    }

    @Override
    public String getValueForHeader(String header) {
      Integer index = headerToIndex.get(header.trim());
      if (null == index) {
        throw new IllegalArgumentException(
            "The header " + header + " , is unknown. Is the CSV well formed? " +
                "Possibly the number of the columns in row exceeds the numbers of columns in the header." +
                "Invalid record was= " + Arrays.toString(record) +
                "Also note, that the CSV without header row is not supported for the moment. ");
      }
      return record[index];
    }
  }

}
