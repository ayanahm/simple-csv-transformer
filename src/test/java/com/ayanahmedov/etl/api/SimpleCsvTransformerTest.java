package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class SimpleCsvTransformerTest {

  @DisplayName("given well formed csv, then output contains all rows correctly mapped by given dsl. ")
  @Test
  void transform_simple_csv_no_invalid_row() throws IOException {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csvFile = FileSystemUtils.getFileFromResources("source-1.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .build();

    try (BufferedReader csvReader = Files.newBufferedReader(csvFile)) {
      StringWriter stringWriter = new StringWriter();
      transformer.transform(csvReader, stringWriter);
      String transformedCsv = stringWriter.toString();
      TestUtils.assertStringEqualsCsv(
          transformedCsv,
          Arrays.asList(
              new String[]{"OrderId","OrderDate","ProductId","ProductName","Quantity","Unit"},
              new String[]{"1000", "2018-01-01", "P-10001", "Arugola", "5250.5", "kg"},
              new String[]{"1001", "2017-12-12", "P-10002", "Iceberg lettuce", "500", "kg"}
          ));
    }
  }

  @DisplayName("given well formed csv, then output csv contains all rows correctly mapped by given dsl. ")
  @Test
  void well_formed_csv_bufferedWriter() throws IOException {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csvFile = FileSystemUtils.getFileFromResources("source-1.csv");
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
            new String[]{"OrderId","OrderDate","ProductId","ProductName","Quantity","Unit"},
            new String[]{"1000", "2018-01-01", "P-10001", "Arugola", "5250.5", "kg"},
            new String[]{"1001", "2017-12-12", "P-10002", "Iceberg lettuce", "500", "kg"}
        ));
  }

  @DisplayName("given a csv with dollar sign as value in it then dont confuse the pattern")
  @Test
  void dolar_sign_as_value() throws IOException {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-for-dollar-sign-test.xml");
    Path csvFile = FileSystemUtils.getFileFromResources("csv-with-dollar-sign-as-value.csv");
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
            new String[]{"formatted"},
            new String[]{"   a$:$a. a$"},
            new String[]{"    $:$1. $"},
            new String[]{"    a:$. a"},
            new String[]{"    $:a. $"},
            new String[]{"    a:a. a"}
        ));
  }
}