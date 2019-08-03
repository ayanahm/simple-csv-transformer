package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class SumColumnsTest {
  @DisplayName("given source csv with headers Col-1,Col-2, then the output contains with header Sum, containing Col1+Col2 ")
  @Test
  void sum_columns_into_single_column() throws IOException {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-sum-columns.xml");
    Path csvFile = FileSystemUtils.getFileFromResources("sum-columns.csv");

    ReducedCsvValueToStringFormatter customFormatter = new ReducedCsvValueToStringFormatter() {
      @Override
      public ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters) {
        return this;
      }

      @Override
      public CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping) {
        String[] split = valueFromCsvMapping.split(",");
        int sum = Integer.parseInt(split[0].trim()) + Integer.parseInt(split[1].trim());
        return CsvValueToJavaMappingResult.ofValue("" + sum);
      }
    };

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withFormatter(customFormatter)
        .withXmlDsl(configFile)
        .build();

    try (BufferedReader csvReader = Files.newBufferedReader(csvFile)) {
      StringWriter stringWriter = new StringWriter();
      transformer.transform(csvReader, stringWriter);
      String transformedCsv = stringWriter.toString();
      TestUtils.assertStringEqualsCsv(
          transformedCsv,
          Arrays.asList(
              new String[]{"Sum"},
              new String[]{"30"},
              new String[]{"50"},
              new String[]{"70"}
          ));
    }
  }
}
