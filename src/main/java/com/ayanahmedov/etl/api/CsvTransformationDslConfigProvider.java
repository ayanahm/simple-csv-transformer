package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;

public interface CsvTransformationDslConfigProvider {
  /**
   * @return dsl for the {@link CsvTransformer}
   */
  CsvTransformationConfig provide();
}
