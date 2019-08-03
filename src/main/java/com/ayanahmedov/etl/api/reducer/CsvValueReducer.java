package com.ayanahmedov.etl.api.reducer;

import java.util.List;

public interface CsvValueReducer {

  /**
   * Reduces the mapped values into single string by underlying pattern.
   *
   * @param mappedValues values mapped or simply taken as is, if no mapper was applicable
   * @param positions    to replace in the underlying pattern. This contains all the positions specified in the DSL.
   */
  String reduce(List<String> mappedValues, List<Integer> positions);
}
