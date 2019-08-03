package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnAverageFormatterTest {

  @DisplayName("Given a csv with 4 columns, then take avarage of specified 2 colums and map into single target column")
  @Test
  public void source_columns_to_avarage_test() throws IOException {
    Path testConfig = FileSystemUtils.getFileFromResources("test-dsl-instance-column-average.xml");
    Path testCsv = FileSystemUtils.getFileFromResources("source-2.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(testConfig)
        .build();

    try (BufferedReader testCsvReader = Files.newBufferedReader(testCsv)) {
      StringWriter stringWriter = new StringWriter();

      transformer.transform(testCsvReader, stringWriter);

      String outCsv = stringWriter.toString();
      String expected = FileSystemUtils.createCsvAsString(Arrays.asList(
          new String[]{"average"},
          new String[]{"25"},
          new String[]{"30"}
      ));

      assertEquals(expected, outCsv);
    }
  }

}
