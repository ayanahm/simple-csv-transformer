package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncorrectCsvColumnValuesTest {
  static Path csv;

  @BeforeAll
  public static void createHugeCsv() {
    csv = FileSystemUtils.createTemporaryCsvFile(writer -> {
      writer.printf("Order Number,Year,Month,Day,Product Number,Product Name   ,Count     ,Extra Col1,Extra Col2,Empty Column%n");

      // some invalid records

      //invalid number a
      writer.printf("%s%n", "a,2018,1,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");

      //invalid number a
      writer.printf("%s%n", "1000,a,1,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");

      //invalid number a
      writer.printf("%s%n", "1000,2018,a,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");

      //invalid number aaa5,250.50
      writer.printf("%s%n", "1000,2018,1,1,P-1000,Arugola,\"aaa5,250.50\",Lorem,Ipsum");

      writer.flush();
      writer.close();
    });
  }

  @Test
  public void test_transformer_can_handle_invalid_records() throws IOException {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(dsl)
        .build();


    try (BufferedReader csvReader = Files.newBufferedReader(csv)) {

      StringWriter stringWriter = new StringWriter();
      transformer.transform(csvReader, stringWriter);
      String csvTransformResult = stringWriter.toString();

      String expected = FileSystemUtils.createCsvAsString(
          Arrays.asList(
              new String[]{ "OrderId","OrderDate","ProductId","ProductName","Quantity","Unit" },
              new String[]{ "","2018-01-01","P-1000","Arugola","5250.5","kg" },
              new String[]{ "1000","","P-1000","Arugola","5250.5","kg" },
              new String[]{ "1000","","P-1000","Arugola","5250.5","kg" },
              new String[]{ "1000","2018-01-01","P-1000","Arugola","","kg" }
          )
      );

      assertEquals(expected, csvTransformResult);
    }
  }
}
