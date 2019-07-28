package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * For manual execution.
 * Not meant as an automated build test.
 */
public class HugeCsvTester {
  private static int NUM_CSV_ROWS = 200_000;

  public static void main(String[] args) throws IOException {
    Path dsl = TestUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path bigCsvFile1 = createTemporaryCsvFileAbout10Megabytes();
    Path bigCsvFile2 = createTemporaryCsvFileAbout10Megabytes();
    Path bigCsvFile3 = createTemporaryCsvFileAbout10Megabytes();
    Path bigCsvFile4 = createTemporaryCsvFileAbout10Megabytes();

    CsvTransformer transformer = CsvTransformerBuilder.builder()
        .withXmlDsl(dsl)
        .build();

    Stream.of(bigCsvFile1, bigCsvFile2, bigCsvFile3, bigCsvFile4)
        .parallel()
        .forEach(path -> {
          Path outputTemp = TestUtils.createTemporaryCsvFile(printer -> {
//      do nothing
          });

          try (
              BufferedReader reader = Files.newBufferedReader(path);
              BufferedWriter writer = Files.newBufferedWriter(outputTemp)) {

            transformer.transform(reader, writer);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    System.out.println("done");
  }

  private static Path createTemporaryCsvFileAbout10Megabytes() {
    return TestUtils.createTemporaryCsvFile(writer -> {
        writer.printf("Order Number,Year,Month,Day,Product Number,Product Name   ,Count     ,Extra Col1,Extra Col2,Empty Column%n");

        //an invalid record
        writer.printf("a,a,a,a,a,a,a,a,a");

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
}
