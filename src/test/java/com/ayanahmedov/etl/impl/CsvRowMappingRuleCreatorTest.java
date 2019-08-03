package com.ayanahmedov.etl.impl;

import com.ayanahmedov.etl.api.dsl.*;
import com.ayanahmedov.etl.api.sourcemapper.SourceColumnMapper;
import com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer;
import com.ayanahmedov.etl.api.tostringformatter.FormattingToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.MappedCsvValueToStringFormatter;
import com.ayanahmedov.etl.api.tostringformatter.SimpleIntFormatter;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvRowMappingRuleCreatorTest {
  SourceHeaderRowAccessor headerAccessor = headerName -> {
    Map<String, Integer> dummy = new HashMap<>();
    dummy.put("C1",0);
    dummy.put("C2",1);
    dummy.put("C3",2);

    return dummy.get(headerName);
  };
  List<SourceColumnMapper> mappers = Arrays.asList(TwoDigitsNormalizer.get());
  List<MappedCsvValueToStringFormatter> formatters = Arrays.asList(
      SimpleIntFormatter.get(),
      FormattingToStringFormatter.getUninitialized());

  @Test
  public void create_rule_from_dsl() {
    CsvRowMappingRuleCreator creator = new CsvRowMappingRuleCreator(headerAccessor, mappers, formatters);

    CsvTransformationConfig dsl = new CsvTransformationConfig()
        .withTransformation(new SourceTransformation()
            .withTargetSchemaColumn("New-C1")
            .withSourceBindPattern("$1,$2")

            .withTargetStringFormatter(new TargetStringFormatter()
                .withFormatterClass(new CustomFormatter()
                    .withClassName(FormattingToStringFormatter.class.getCanonicalName())
                    .withParameter(new FormatterParameter()
                        .withName("string-format")
                        .withValue("%s--%s"))))

            .withSourceColumn(new SourceCsvColumn()
              .withBindPatternPosition(1)
              .withName("C1")
              .withSourceValueMapper(TwoDigitsNormalizer.class.getName()))

            .withSourceColumn(new SourceCsvColumn()
              .withBindPatternPosition(2)
              .withName("C2"))
        );

    List<CsvRowMappingRule> rules = creator.createRules(dsl);

    assertEquals(1, rules.size());

    CsvRowMappingRule rule = rules.get(0);

    assertEquals(Arrays.asList(1,2), rule.getBindPatternPositions());
    assertEquals(Arrays.asList(0,1), rule.getSourceColumnIndexes());
    assertEquals(Collections.singletonMap(0, TwoDigitsNormalizer.get()), rule.getMappersByIndex());
    assertTrue(rule.getFormatter() instanceof FormattingToStringFormatter);
    assertTrue(rule.getReducer() instanceof DollarSignBindPatternReducer);
  }

}