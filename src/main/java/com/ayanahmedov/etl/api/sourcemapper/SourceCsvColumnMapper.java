package com.ayanahmedov.etl.api.sourcemapper;

/**
 * The mapper of the source CSV value
 * before applied into the formatters .
 */
public interface SourceCsvColumnMapper {

  /**
   * Applied on the source csv value
   * before passed into formatters.
   * The output value is supplied into the corresponding formatter.
   *
   * @param csvSourceValue the string value read as-is.
   * @return value to supply into the formatters.
   */
  String map(String csvSourceValue);
}
