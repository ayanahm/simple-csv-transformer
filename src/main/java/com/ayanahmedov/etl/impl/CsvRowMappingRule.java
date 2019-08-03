package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.reducer.CsvValueReducer;
import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;

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
  Map<Integer, SourceCsvColumnMapper> getMappersByIndex();

  /**
   * Reducer instance constructed from DSL.
   */
  CsvValueReducer getReducer();

  /**
   * Formatter to apply on the reduced value.
   */
  ReducedCsvValueToStringFormatter getFormatter();
}
