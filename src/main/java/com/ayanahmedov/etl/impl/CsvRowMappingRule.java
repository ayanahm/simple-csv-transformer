package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.sourcemapper.SourceColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;

import java.util.List;
import java.util.Map;

public interface CsvRowMappingRule {

  /**
   * indexes on the source column scanned from the DSL.
   */
  List<Integer> getSourceColumnIndexes();

  /**
   * Positions from DSL to be replaced in the reducer {@link CsvValueReducer}.
   */
  List<Integer> getBindPatternPositions();

  /**
   * Mappers applicable to the column identifiable by the returned maps key.
   */
  Map<Integer, SourceColumnMapper> getMappersByIndex();

  /**
   * Reducer instance constructed from DSL.
   */
  CsvValueReducer getReducer();

  /**
   * Formatter to apply on the reduced value.
   */
  MappedCsvValueToStringFormatter getFormatter();
}
