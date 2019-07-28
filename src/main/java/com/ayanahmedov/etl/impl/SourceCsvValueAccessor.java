package com.ayanahmedov.etl.impl;

public interface SourceCsvValueAccessor {
  String getValueForHeader(String sourceCsvHeader);
}
