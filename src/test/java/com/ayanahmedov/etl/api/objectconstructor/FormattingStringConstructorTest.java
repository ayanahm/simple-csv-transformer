package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.TestUtils;
import com.ayanahmedov.etl.api.CsvTransformer;
import com.ayanahmedov.etl.api.CsvTransformerBuilder;
import com.ayanahmedov.etl.api.DslConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FormattingStringConstructorTest {

  @DisplayName("Given a csv with 2 columns, then take format 2 colums and map into single target column")
  @Test
  public void source_columns_to_avarage_test() throws IOException {
    Path testConfig = TestUtils.getFileFromResources("test-dsl-instance-string-formatter.xml");
    Path testCsv = TestUtils.getFileFromResources("source-3.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(testConfig)
        .build();

    try (BufferedReader testCsvReader = Files.newBufferedReader(testCsv)) {
      StringWriter stringWriter = new StringWriter();

      transformer.transform(testCsvReader, stringWriter);

      String outCsv = stringWriter.toString();

      String expected = TestUtils.createCsvAsString(Arrays.asList(
          new String[]{"formatted"},
          new String[]{"    1:a.  1"},
          new String[]{"   10:b.  10"},
          new String[]{"    9:c.  9"}
      ));

      assertEquals(expected, outCsv);
    }
  }

  @Test
  public void invalid_formatter_then_configException_is_thrown() throws IOException {
    Path testConfig = TestUtils.getFileFromResources("test-dsl-instance-string-formatter-invalid.xml");
    Path testCsv = TestUtils.getFileFromResources("source-3.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(testConfig)
        .build();

    try (BufferedReader testCsvReader = Files.newBufferedReader(testCsv)) {
      StringWriter stringWriter = new StringWriter();

      assertThrows(DslConfigurationException.class, () -> transformer.transform(testCsvReader, stringWriter));

    }
  }

}
