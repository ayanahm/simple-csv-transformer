package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.TestUtils;
import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import com.ayanahmedov.etl.api.dsl.DateValueConstructor;
import com.ayanahmedov.etl.api.dsl.ElementConstructor;
import com.ayanahmedov.etl.api.dsl.SourceCsvColumn;
import com.ayanahmedov.etl.api.dsl.SourceTransformation;
import com.ayanahmedov.etl.api.dsl.TargetDateFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlDslConfigProviderTest {

  @DisplayName("Given a well formed DSL as xml-file, parsed object is as expected")
  @Test
  public void xml_dsl_read_is_correct1() {
    CsvTransformationConfig expect = new CsvTransformationConfig()
        .withTransformation(new SourceTransformation()
          .withTargetSchemaColumn("target-col-1")
          .withElementConstructor(new ElementConstructor()
            .withSourceBindPattern("$1")
            .withDateValueConstructor(new DateValueConstructor()
              .withSourceFormatPattern("yyyy-dd-MM")
              .withTargetDateFormat(new TargetDateFormat()
                .withFormatPattern("yyyy:LL:dd")
                .withZoneId("Europe/Vienna"))))
          .withSourceColumn(new SourceCsvColumn()
            .withName("source-csv-col-1")
            .withConstructorPosition(1)));

    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-instance-2.xml");
    XmlDslConfigProvider xmlDslConfigProvider = new XmlDslConfigProvider(configFile);

    TestUtils.assertXmlEquals(expect, xmlDslConfigProvider.provide());
  }

  @Test
  public void invalid_xml_instance_for_dsl_then_validation_exception_is_thrown() {
    Path configFile = FileSystemUtils.getFileFromResources("test-dsl-invalid-dsl-instance.xml");
    XmlDslConfigProvider xmlDslConfigProvider = new XmlDslConfigProvider(configFile);
    assertThrows(DslConfigurationException.class, xmlDslConfigProvider::provide);
  }
}