package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class NotWellFormedCsvTest {

  @Test
  public void test_on_not_well_formed_csv_record_is_skipped() throws IOException {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path csv = FileSystemUtils.getFileFromResources("not-well-formed-csv-1.csv");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(dsl)
        .build();


    try (BufferedReader csvReader = Files.newBufferedReader(csv)) {

      StringWriter stringWriter = new StringWriter();
      try {
        transformer.transform(csvReader, stringWriter);
        fail("exception is expected");
      } catch (IllegalArgumentException e) {
        String msg = e.getMessage();
        String expectedMsg = "Cannot get the value from the CSV row. CSV is not well formed. The row does not match the size of the header. row is=[Ipsum, ]";
        assertEquals(expectedMsg, msg);
      }
    }
  }

}
