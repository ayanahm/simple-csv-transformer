package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XmlDslConfigProvider implements CsvTransformationDslConfigProvider {
  private final Path configXmlFilePath;

  public XmlDslConfigProvider(Path configXmlFilePath) {
    this.configXmlFilePath = configXmlFilePath;
  }

  @Override
  public CsvTransformationConfig provide() {
    try {
      String xml = new String(Files.readAllBytes(configXmlFilePath), StandardCharsets.UTF_8);
      return JaxbUtils.parseObject(xml, CsvTransformationConfig.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
