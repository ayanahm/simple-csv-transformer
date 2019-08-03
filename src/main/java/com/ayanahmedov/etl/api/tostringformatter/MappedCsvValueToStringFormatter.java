package com.ayanahmedov.etl.api.tostringformatter;

import com.ayanahmedov.etl.api.dsl.TargetStringFormatter;
import com.ayanahmedov.etl.impl.CsvValueToJavaMappingResult;

import java.util.Map;

public interface MappedCsvValueToStringFormatter {

  /**
   *
   * Formats the csv columns into a string value, which to be written into the target CSV
   * after applying {@link com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper}
   * if there are any applicable.
   *
   * By implementing this interface and providing it to the {@link com.ayanahmedov.etl.api.CsvTransformer}
   * the target csv columns can be calculated in a custom way.
   *
   * @param valueFromCsvMapping contains bound value  as defined in {@link TargetStringFormatter#getSourceBindPattern()}
   *                            and after applying any {@link com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper}
   *                            defined for the source columns.
   *
   * <p/>
   * Example given a CSV with a single row,
   *
   * <p/>
   * <pre>
   *   col1,col2,col3
   *   abc,200,0
   * </pre>
   *   And the bind pattern configured in {@link TargetStringFormatter#getSourceBindPattern()}
   * to be
   * <pre>
   *   $1:$2-$3
   * </pre>
   * and there is source {@link com.ayanahmedov.etl.api.sourcemapper.SourceValueMapper}
   * defined for col3, which turns 0 into 00.
   * Then
   * <p/>
   * the input parameterFromSources will contain the value:
   * <pre>
   * abc:200-00
   * </pre>
   *
   *
   * @param parameters the parameters defined in the DSL will be passed. See also {@link com.ayanahmedov.etl.api.dsl.CustomFormatter}
   *
   * @return formatted string
   * or an exception if during the formatting an invalid value is encountered in the csv.
   */
  CsvValueToJavaMappingResult formatToString(String valueFromCsvMapping, Map<String, String> parameters);
}
