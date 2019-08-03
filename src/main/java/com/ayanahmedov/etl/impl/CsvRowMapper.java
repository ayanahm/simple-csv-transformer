package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.reducer.CsvValueReducer;
import com.ayanahmedov.etl.api.sourcemapper.SourceCsvColumnMapper;
import com.ayanahmedov.etl.api.tostringformatter.ReducedCsvValueToStringFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvRowMapper {
  private final static Logger log = Logger.getLogger(CsvRowMapper.class.getName());

  private final List<CsvRowMappingRule> rules;
  private final int targetRowLength;

  public CsvRowMapper(List<CsvRowMappingRule> rules, int targetRowLength) {
    this.rules = rules;
    this.targetRowLength = targetRowLength;
  }

  public String[] mapRow(String[] sourceRow) {
    String[] result = new String[targetRowLength];

    for (int i = 0; i < rules.size(); i++) {
      CsvRowMappingRule rule = rules.get(i);
      List<Integer> relevantColumnIndexes = rule.getSourceColumnIndexes();
      List<Integer> bindPatternPositions = rule.getBindPatternPositions();
      Map<Integer, SourceCsvColumnMapper> mappersByIndex = rule.getMappersByIndex();

      List<String> mappedValues = new ArrayList<>();

      for (Integer index : relevantColumnIndexes) {
        SourceCsvColumnMapper mapper = mappersByIndex.get(index);
        String value = getColumn(sourceRow, index);
        if (mapper != null) {
          value = mapper.map(value);
        }
        mappedValues.add(value);
      }

      CsvValueReducer reducer = rule.getReducer();
      String reducedVal = reducer.reduce(mappedValues, bindPatternPositions);

      ReducedCsvValueToStringFormatter formatter = rule.getFormatter();
      CsvValueToJavaMappingResult formatted = formatter.formatToString(reducedVal);

      final int index = i;
      formatted.getValue().ifPresent(val -> result[index] = val);

      formatted.getException().ifPresent(exp -> {
        result[index] = "";
        log.log(Level.WARNING,
            "the formatting could not be applied on SourceColumns:[{0}]. With reduced value=[{1}]  resulted in exception {2}",
            new Object[]{
                relevantColumnIndexes,
                reducedVal,
                exp
            });
      });
    }

    return result;
  }

  private String getColumn(String[] sourceRow, Integer index) {
    if (sourceRow.length <= index) {
      throw new IllegalArgumentException("Cannot get the value from the CSV row. CSV is not well formed. " +
          "The row does not match the size of the header. row is=" + Arrays.toString(sourceRow));
    }
    return sourceRow[index];
  }

}
