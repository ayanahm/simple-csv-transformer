package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.TestUtils;
import com.ayanahmedov.etl.api.CsvTransformer;
import com.ayanahmedov.etl.api.CsvTransformerBuilder;
import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

class DefaultCsvTransformerTest {

  @DisplayName("given well formed csv, then output contains all rows correctly mapped by given dsl. ")
  @Test
  void transform_simple_csv_no_invalid_row() throws IOException {
    Path configFile = TestUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csvFile = TestUtils.getFileFromResources("source-1.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .withSourceCsvValueMappers(Collections.singletonList(new TwoDigitsNormalizer()))
        .withObjectConstructors(Collections.emptyList())
        .build();

    try (BufferedReader csvReader = Files.newBufferedReader(csvFile)) {
      StringWriter stringWriter = new StringWriter();
      transformer.transform(csvReader, stringWriter);
      String transformedCsv = stringWriter.toString();
      TestUtils.assertStringEqualsCsv(
          transformedCsv,
          Arrays.asList(
              new String[]{"ProductName", "Quantity", "ProductId", "OrderId", "Unit", "OrderDate"},
              new String[]{"1000", "2018-01-01", "P-10001", "Arugola", "5250.5", "kg"},
              new String[]{"1001", "2017-12-12", "P-10002", "Iceberg lettuce", "500", "kg"}
          ));
    }
  }

  @DisplayName("given well formed csv, then output csv contains all rows correctly mapped by given dsl. ")
  @Test
  void well_formed_csv_bufferedWriter() throws IOException {
    Path configFile = TestUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csvFile = TestUtils.getFileFromResources("source-1.csv");
    Path outputFile = TestUtils.createTemporaryCsvFile(PrintWriter::flush/*do nothing*/);

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .withSourceCsvValueMappers(Collections.singletonList(new TwoDigitsNormalizer()))
        .withObjectConstructors(Collections.emptyList())
        .build();

    try (BufferedReader csvReader = Files.newBufferedReader(csvFile);
         BufferedWriter csvWriter = Files.newBufferedWriter(outputFile)) {

      transformer.transform(csvReader, csvWriter);
    }

    String savedCsv = new String(Files.readAllBytes(outputFile));

    TestUtils.assertStringEqualsCsv(
        savedCsv,
        Arrays.asList(
            new String[]{"ProductName", "Quantity", "ProductId", "OrderId", "Unit", "OrderDate"},
            new String[]{"1000", "2018-01-01", "P-10001", "Arugola", "5250.5", "kg"},
            new String[]{"1001", "2017-12-12", "P-10002", "Iceberg lettuce", "500", "kg"}
        ));
  }

}