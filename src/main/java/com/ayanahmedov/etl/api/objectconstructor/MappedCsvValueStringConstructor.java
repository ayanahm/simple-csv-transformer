package com.ayanahmedov.etl.api.objectconstructor;

import com.ayanahmedov.etl.api.dsl.ElementConstructor;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public interface MappedCsvValueStringConstructor {

  /**
   *
   * The combined string value passed from the CsvSources
   * after applying {@link com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper}
   * if there are any applicaple.
   *
   * <p/>
   * Example:
   * source Csv column values referenced from the configuration
   * (see {@link SourceTransformation#getSourceColumn()} or {@link SourceTransformation#getConstantSource()}
   * have the values in the currently mapped csv-row:
   * <p/>
   * <pre>
   * source-column-1 : abc
   * source-column-2 : 200
   * source-column-3 : 0
   * </pre>
 *   And the bind pattern configured in {@link ElementConstructor#getSourceBindPattern()}
   * to be
   * <pre>
   *   $1:$2-$3
   * </pre>
   * <p/>
   * the input @param parameterFromSources will contain the value:
   * <pre>
   * abc:200-0
   * </pre>
   *
   */
  CsvValueToJavaMappingResult constructString(String valueFromCsvMapping, Map<String, String> parameters);
}
