package com.ayanahmedov.etl.api;

import java.io.Reader;
import java.io.Writer;

public interface CsvTransformer{
  /**
   * Reads CSV from the csvReader
   * and writes the transformed CSV into csvWriter.
   *
   *
   * Note the source CSV is restricted to contain a header, which is not strictly required by CSV specifications.
   * If csvReader provides a CSV without any header row, then the behaviour is not defined.
   *
   * @param csvReader the reader where the source csv to transform can be read from.
   * @param csvWriter the writer where the output csv to write into.
   */
  void transform(Reader csvReader, Writer csvWriter);
}
