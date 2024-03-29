package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateByDateTimePatternFormatterTest {

  @Test
  public void date_formatter_test() throws IOException {
    Path testConfig = FileSystemUtils.getFileFromResources("test-dsl-instance-date-formatter.xml");
    Path testCsv = FileSystemUtils.getFileFromResources("source-4.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(testConfig)
        .build();

    try (BufferedReader testCsvReader = Files.newBufferedReader(testCsv)) {
      StringWriter stringWriter = new StringWriter();

      transformer.transform(testCsvReader, stringWriter);

      String outCsv = stringWriter.toString();

      String expected = FileSystemUtils.createCsvAsString(Arrays.asList(
          new String[]{"yyyy-MM-dd","yyyy-MM-dd:HH","yyyy-MM-dd zz","yyyy-MM-dd:HH zz","yyyy-MM-dd:HH:mm zz"},
          new String[]{"2018:02:06","2018-06-00:10","2018-02-05 Europe/Vienna","2018-02-06:09 Europe/Vienna","2018-02-06:09 Europe/Vienna"},
          new String[]{"2010:01:12","2010-12-00:11","2010-01-11 Europe/Vienna","2010-01-12:22 Europe/Vienna","2010-01-12:22 Europe/Vienna"}
      ));
      assertEquals(expected, outCsv);
    }
  }

  @Test
  public void invalid_dsl_exception_is_thrown() throws IOException {
    Path testConfig = FileSystemUtils.getFileFromResources("test-dsl-instance-date-formatter-invalid-1.xml");
    Path testCsv = FileSystemUtils.getFileFromResources("source-4.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(testConfig)
        .build();

    try (BufferedReader testCsvReader = Files.newBufferedReader(testCsv)) {
      StringWriter stringWriter = new StringWriter();

      assertThrows(DslConfigurationException.class,
          () -> transformer.transform(testCsvReader, stringWriter));
    }
  }
}