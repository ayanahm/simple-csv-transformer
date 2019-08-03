package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class BigDecimalFormatterTest {

  @Test
  void print_big_decimal_with_target_locale_and_without() throws IOException {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-big-decimal-formatter.xml");
    Path csvFile = FileSystemUtils.getFileFromResources("source-big-decimal.csv");
    Path outputFile = FileSystemUtils.createTemporaryCsvFile(PrintWriter::flush/*do nothing*/);

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .build();

    try (BufferedReader csvReader = Files.newBufferedReader(csvFile);
         BufferedWriter csvWriter = Files.newBufferedWriter(outputFile)) {

      transformer.transform(csvReader, csvWriter);
    }

    String savedCsv = new String(Files.readAllBytes(outputFile));

    TestUtils.assertStringEqualsCsv(
        savedCsv,
        Arrays.asList(
            new String[]{"locale_DE", "locale_US"},
            new String[]{"5.250,5", "5,250.5"},
            new String[]{"52.250,5", "52,250.5"}
        ));
  }
}