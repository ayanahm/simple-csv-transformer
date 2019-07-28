package com.ayanahmedov.etl;

import com.ayanahmedov.etl.api.JaxbUtils;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {
  public static Path getFileFromResources(String resourcePath) {
    URL configUrl = TestUtils.class.getClassLoader()
        .getResource(resourcePath);
    if (configUrl == null) {
      throw new IllegalArgumentException("Cannot find the resource file by path:" + resourcePath);
    }
    try {
      return Paths.get(configUrl.toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }


  public static String readLastLineOfFile(Path outputTemp) {
    RandomAccessFile raf = null;
    try {
      StringBuilder result = new StringBuilder();
      raf = new RandomAccessFile(outputTemp.toFile(), "r");
      long fileSize = raf.length();
      for (long position = fileSize - 1; position != -1; position--) {
        raf.seek(position);
        byte b = raf.readByte();
        char c = (char) (b & 0xFF);
        if ('\n' == c) {
          if (fileSize - 1 == position) {
            //for the very last new lines we don't want to terminate
            continue;
          }
          //breaks without checking \r, such that if ends with \r\n
          break;
        } else if ('\r' == c) {
          if (fileSize - 1 == position) {
            //for the very last new lines we don't want to terminate
            continue;
          }
          break;
        } else {
          result.append(c);
        }
      }
      return result.reverse().toString();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      try {
        if (raf != null) {
          raf.close();
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  public static Path createTemporaryCsvFile(Consumer<PrintWriter> withPrintWriter) {
    try {
      Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".csv");
      temp.toFile().deleteOnExit();

      try (BufferedWriter bufferedWriter = Files.newBufferedWriter(temp);
           PrintWriter writer = new PrintWriter(bufferedWriter)) {
        withPrintWriter.accept(writer);

        temp.toFile().deleteOnExit();
        return temp;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String createCsvAsString(List<String[]> records) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    CSVWriter csvWriter = new CSVWriter(printWriter);
    csvWriter.writeAll(records);
    return stringWriter.toString();
  }

  public static void assertStringEqualsCsv(String actual, List<String[]> expectedCsv) {
    String expected = createCsvAsString(expectedCsv);
    assertEquals(expected.trim(), actual.trim());
  }

  public static void assertXmlEquals(Object expect, Object actual) {
    assertEquals(JaxbUtils.parseXml(expect), JaxbUtils.parseXml(actual));
  }

}
