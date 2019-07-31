package com.ayanahmedov.etl.api;

import com.ayanahmedov.etl.api.dsl.CsvTransformationConfig;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
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
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Path xsdFile = FileSystemUtils.getFileFromResources("csv-transform-schema.xsd");
      Schema schema = schemaFactory.newSchema(xsdFile.toFile());
      return JaxbUtils.parseObjectValidatingSchema(xml, CsvTransformationConfig.class, schema);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }
}
