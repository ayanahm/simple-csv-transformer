package com.ayanahmedov.etl.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * For manual execution.
 * Not meant as an automated build test.
 */
public class ManualCsvTransormInvoker {
  private static int NUM_CSV_ROWS = 1_000_000;

  public static void main(String[] args) {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-string-formatter.xml");
    Path csv = FileSystemUtils.getFileFromResources("source-2.csv");
    Path output = Paths.get("output2.csv");

    CsvTransformer transformer = CsvTransformerBuilder.builder()
        .withXmlDsl(dsl)
        .build();

    try (
        BufferedReader reader = Files.newBufferedReader(csv);
        BufferedWriter writer = Files.newBufferedWriter(output)) {

      transformer.transform(reader, writer);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    System.out.println("done");
  }

  public static void _main(String[] args) {
    Path dsl = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");
    Path bigCsvFile1 = createTemporaryHugeCsvFile();
    Path bigCsvFile2 = createTemporaryHugeCsvFile();
    Path bigCsvFile3 = createTemporaryHugeCsvFile();
    Path bigCsvFile4 = createTemporaryHugeCsvFile();

    CsvTransformer transformer = CsvTransformerBuilder.builder()
        .withXmlDsl(dsl)
        .build();

    Stream.of(bigCsvFile1, bigCsvFile2, bigCsvFile3, bigCsvFile4)
        .parallel()
        .forEach(path -> {
          Path outputTemp = FileSystemUtils.createTemporaryCsvFile(printer -> {
//      do nothing
          });

          try (
              BufferedReader reader = Files.newBufferedReader(path);
              BufferedWriter writer = Files.newBufferedWriter(outputTemp)) {

            transformer.transform(reader, writer);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });

    System.out.println("done");
  }

  private static Path createTemporaryHugeCsvFile() {
    return FileSystemUtils.createTemporaryCsvFile(writer -> {
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
