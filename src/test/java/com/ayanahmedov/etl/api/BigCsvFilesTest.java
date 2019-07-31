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
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

public class BigCsvFilesTest {
  private static int NUM_CSV_ROWS = 100_00;

  private static Path bigCsvPath;

  @BeforeAll
  public static void createHugeCsv() {
    bigCsvPath = FileSystemUtils.createTemporaryCsvFile(writer -> {
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
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-1.xml");

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(configFile)
        .withSourceCsvValueMappers(Collections.singletonList(new TwoDigitsNormalizer()))
        .withObjectConstructors(Collections.emptyList())
        .build();

    Path outputTemp = FileSystemUtils.createTemporaryCsvFile(printer -> {
//      do nothing
    });

    try (
        BufferedWriter writer = Files.newBufferedWriter(outputTemp);
        BufferedReader reader = Files.newBufferedReader(bigCsvPath)) {

      transformer.transform(reader, writer);
    }

    BufferedReader reader = Files.newBufferedReader(outputTemp);
    String firstLine = reader.readLine();
    String lastLine = FileSystemUtils.readLastLineOfFile(outputTemp);

    TestUtils.assertStringEqualsCsv(firstLine, Collections.singletonList(
        new String[]{"ProductName", "Quantity", "ProductId", "OrderId", "Unit", "OrderDate"}
    ));

    TestUtils.assertStringEqualsCsv(lastLine, Collections.singletonList(
        new String[]{"1000","2018-01-01","P-1000","Arugola","5250.5","kg"}
    ));
  }

  @Timeout(value = 2, unit = TimeUnit.MINUTES)
  @DisplayName("transform big sourceCsv to targetCsv with same source and 3 transformers in parallel.")
  @RepeatedTest(50)
  public void concurrent_transformations_with_different_dsls() throws IOException {
    Path configFile1 = FileSystemUtils.getFileFromResources("test-dsl-instance-concurrent-1.xml");
    Path configFile2 = FileSystemUtils.getFileFromResources("test-dsl-instance-concurrent-2.xml");
    Path configFile3 = FileSystemUtils.getFileFromResources("test-dsl-instance-concurrent-3.xml");

    CsvTransformer transformer1 = new CsvTransformerBuilder()
        .withXmlDsl(configFile1)
        .withObjectConstructors(Collections.emptyList())
        .build();

    CsvTransformer transformer2 = new CsvTransformerBuilder()
        .withXmlDsl(configFile2)
        .withObjectConstructors(Collections.emptyList())
        .build();

    CsvTransformer transformer3 = new CsvTransformerBuilder()
        .withXmlDsl(configFile3)
        .withObjectConstructors(Collections.emptyList())
        .build();


    Path outputTemp1 = FileSystemUtils.createTemporaryCsvFile(printer -> {
//      do nothing
    });

    Path outputTemp2 = FileSystemUtils.createTemporaryCsvFile(printer -> {
//      do nothing
    });

    Path outputTemp3 = FileSystemUtils.createTemporaryCsvFile(printer -> {
//      do nothing
    });

    try (
        BufferedWriter writer1 = Files.newBufferedWriter(outputTemp1);
        BufferedWriter writer2 = Files.newBufferedWriter(outputTemp2);
        BufferedWriter writer3 = Files.newBufferedWriter(outputTemp3);
        BufferedReader reader1 = Files.newBufferedReader(bigCsvPath);
        BufferedReader reader2 = Files.newBufferedReader(bigCsvPath);
        BufferedReader reader3 = Files.newBufferedReader(bigCsvPath)) {


      Stream.of(
          TransformConcurrentContainer.of(transformer1, writer1, reader1),
          TransformConcurrentContainer.of(transformer2, writer2, reader2),
          TransformConcurrentContainer.of(transformer3, writer3, reader3))
          .parallel()
          .forEach(pair -> pair.transformer.transform(pair.csvReader, pair.outputWriter));
    }

    BufferedReader outputReader1 = Files.newBufferedReader(outputTemp1);
    BufferedReader outputReader2 = Files.newBufferedReader(outputTemp2);
    BufferedReader outputReader3 = Files.newBufferedReader(outputTemp3);

    assertAll(Arrays.asList(
        () ->  TestUtils.assertStringEqualsCsv(outputReader1.readLine(), Collections.singletonList(
            new String[]{"Unit","OrderDate"}
        )),

        () ->  TestUtils.assertStringEqualsCsv(outputReader2.readLine(), Collections.singletonList(
            new String[]{"Unit","OrderDate"}
        )),

        () -> TestUtils.assertStringEqualsCsv(outputReader3.readLine(), Collections.singletonList(
            new String[]{"Unit","OrderDate"}
        )),

        () -> TestUtils.assertStringEqualsCsv(FileSystemUtils.readLastLineOfFile(outputTemp1), Collections.singletonList(
            new String[]{"01-01-2018","gram"}
        )),

        () -> TestUtils.assertStringEqualsCsv(FileSystemUtils.readLastLineOfFile(outputTemp2), Collections.singletonList(
            new String[]{"01-01-2018","pound"}
        )),

        () -> TestUtils.assertStringEqualsCsv(FileSystemUtils.readLastLineOfFile(outputTemp3), Collections.singletonList(
            new String[]{"01-01-2018","ton"}
        ))
    ));
  }

  static class TransformConcurrentContainer {
    CsvTransformer transformer;
    Writer outputWriter;
    Reader csvReader;

    public static TransformConcurrentContainer of(CsvTransformer transformer, Writer outputWriter, Reader csvReader) {
      TransformConcurrentContainer p = new TransformConcurrentContainer();
      p.transformer = transformer;
      p.outputWriter = outputWriter;
      p.csvReader = csvReader;
      return p;
    }
  }
}
