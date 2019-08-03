package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class IncorrectDslInstanceTest {

  @Test
  public void incorrect_headers_then_helpful_exception_is_thrown() throws IOException {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-incorrect-source-headers.xml");
    Path csv = FileSystemUtils.getFileFromResources("source-1.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(dsl)
        .build();


    try (BufferedReader csvReader = Files.newBufferedReader(csv)) {

      StringWriter stringWriter = new StringWriter();
      try {
        transformer.transform(csvReader, stringWriter);
        fail("exception is expected");
      } catch (DslConfigurationException e) {
        String msg = e.getMessage();
        String expectedMsg = "Is the DSL well formed? Make sure to reference only the existing headers in the source CSV. The header [THIS DOES NOT EXISTS] is unknown.";
        assertEquals(expectedMsg, msg);
      }
    }
  }

  @Test
  public void incorrect_bind_pattern_position_then_helpful_exception_is_thrown() throws IOException {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-incorrect-bind-pattern.xml");
    Path csv = FileSystemUtils.getFileFromResources("source-2.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(dsl)
        .build();


    try (BufferedReader csvReader = Files.newBufferedReader(csv)) {

      StringWriter stringWriter = new StringWriter();
      try {
        transformer.transform(csvReader, stringWriter);
        fail("exception is expected");
      } catch (DslConfigurationException e) {
        String msg = e.getMessage();
        String expectedMsg =
            "Is the DSL well formed? Make sure to construct bind pattern correctly." +
                " Not all the placeholders in the pattern are covered. " +
                "Make sure to reference with $<num> correct mappings from the source. expected to have = [[1, 6, 1]] but got [[1, 2]].";
        assertEquals(expectedMsg, msg);
      }
    }
  }

}
