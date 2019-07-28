package com.ayanahmedov.etl.api;

import java.io.Reader;
import java.io.Writer;

public interface CsvTransformer{
  /**
   * Transforms the
   */
  void transform(Reader csvReader, Writer csvWriter);
}
