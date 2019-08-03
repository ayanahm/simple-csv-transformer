package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public interface ReducedCsvValueToStringFormatter {

  /**
   * Returns a new instance initialized with parameters.
   * In case no parameter is expected, it is thread-safe to simply return this or a singleton.
   * <p>
   * In case when there are fields stored, this must return a new instance and not a cached instance.
   * <p>
   * Otherwise there is no guarantee for thread-safety.
   */
  ReducedCsvValueToStringFormatter newInstance(Map<String, String> parameters);

  /**
   *
   * Formats the csv columns into a string value, which to be written into the target CSV.
   *
   * By implementing this interface and providing it to the {@link com.ayanahmedov.etl.api.CsvTransformer}
   * the target csv columns can be calculated in a custom way.
   *
   * @param valueFromCsvMapping contains reduced value as defined by {@link com.ayanahmedov.etl.api.reducer.CsvValueReducer}
   *
   *                            defined for the source columns.
   *
   * <p/>
   * Example given a CSV with a single row,
   *
   * <p/>
   * <pre>
   *   col1,col2,col3
   * </pre>
   * <pre>
   *   abc,200,0
   * </pre>
   *
   *
   *   And the bind pattern configured in {@link com.ayanahmedov.etl.api.dsl.SourceTransformation#getSourceBindPattern()}
   * to be
   * <pre>
   *   $1:$2-$3
   * </pre>
   * and there is source {@link SourceCsvColumnMapper}
   * defined for col3, which turns 0 into 00.
   * Then
   * <p/>
   * the input parameterFromSources will contain the value:
   * <pre>
   * abc:200-00
   * </pre>
   *
   *
   * @return formatted string
   * or an exception if during the formatting an invalid value is encountered in the csv.
   */
  CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping);
}
