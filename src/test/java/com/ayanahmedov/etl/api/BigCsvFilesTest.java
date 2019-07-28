package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;
import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BigCsvFilesTest {
  private static int NUM_CSV_ROWS = 100_00;

  private static Path bigCsvPath;
  private static Path configFile = TestUtils.getFileFromResources("test-dsl-instance-1.xml");

  @BeforeAll
  public static void createHugeCsv() {
    bigCsvPath = TestUtils.createTemporaryCsvFile(writer -> {
      writer.printf("Order Number,Year,Month,Day,Product Number,Product Name   ,Count     ,Extra Col1,Extra Col2,Empty Column%n");

      // some invalid records
      writer.printf("%s%n", "a,2018,1,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");
      writer.printf("%s%n", "1000,a,1,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");
      writer.printf("%s%n", "1000,2018,a,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");
      writer.printf("%s%n", "1000,2018,1,1,P-1000,Arugola,\"aaa5,250.50\",Lorem,Ipsum");

      Map<Integer, String> records = new HashMap<Integer, String>() {{
        put(0, "1000,2018,1,1,P-1000,Arugola,\"5,250.50\",Lorem,Ipsum");
        put(1, "1001,2016,10,1,1001-P2,Lettuce,\"500.0\",Lorem,Ipsum");
        put(2, "1002,2017,1,12,P-1002,Tomato,\"367.5\",Lorem,Ipsum");
      }};

      for (int i = 0; i < NUM_CSV_ROWS; i++) {
        writer.printf("%s%n", records.get(i % 3));
      }

      writer.flush();
      writer.close();
    });
  }

  @Timeout(value = 2, unit = TimeUnit.MINUTES)
  @DisplayName("transform big sourceCsv to targetCsv")
  @RepeatedTest(50)
  public void transform_Big_Csv_to_targetCsvFile() throws IOException {
    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .withSourceCsvValueMappers(Collections.singletonList(new TwoDigitsNormalizer()))
        .withObjectConstructors(Collections.emptyList())
        .build();

    Path outputTemp = TestUtils.createTemporaryCsvFile(printer -> {
//      do nothing
    });

    try (
        BufferedWriter writer = Files.newBufferedWriter(outputTemp);
        BufferedReader reader = Files.newBufferedReader(bigCsvPath)) {

      transformer.transform(reader, writer);
    }

    BufferedReader reader = Files.newBufferedReader(outputTemp);
    String firstLine = reader.readLine();
    String lastLine = TestUtils.readLastLineOfFile(outputTemp);

    TestUtils.assertStringEqualsCsv(firstLine, Collections.singletonList(
        new String[]{"ProductName", "Quantity", "ProductId", "OrderId", "Unit", "OrderDate"}
    ));

    TestUtils.assertStringEqualsCsv(lastLine, Collections.singletonList(
        new String[]{"1000","2018-01-01","P-1000","Arugola","5250.5","kg"}
    ));
  }
}
