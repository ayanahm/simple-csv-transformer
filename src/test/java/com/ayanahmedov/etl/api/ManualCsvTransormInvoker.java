package com.ayanahmedov.etl.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * For manual execution.
 * Not meant as an automated build test.
 */
public class ManualCsvTransormInvoker {
  public static void main(String[] args) {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csv = FileSystemUtils.getFileFromResources("source-1.csv");
    CsvTransformer transformer = CsvTransformerBuilder.builder()
        .withXmlDsl(dsl)
        .build();

    try (
        BufferedReader reader = Files.newBufferedReader(csv);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("sample-out.csv"))) {

      transformer.transform(reader, writer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
