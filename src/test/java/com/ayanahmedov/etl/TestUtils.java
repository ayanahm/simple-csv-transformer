package com.ayanahmedov.etl;

import com.ayanahmedov.etl.api.FileSystemUtils;
import com.ayanahmedov.etl.api.JaxbUtils;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class TestUtils extends FileSystemUtils {

  public static void assertStringEqualsCsv(String actual, List<String[]> expectedCsv) {
    String expected = createCsvAsString(expectedCsv);
    Assertions.assertEquals(expected.trim(), actual.trim());
  }

  public static void assertXmlEquals(Object expect, Object actual) {
    Assertions.assertEquals(JaxbUtils.parseXml(expect), JaxbUtils.parseXml(actual));
  }
}
