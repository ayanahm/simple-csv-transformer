package com.ayanahmedov.etl.api;

import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JaxbUtils {
  private static ConcurrentHashMap<Class<?>, JAXBContext> JAXB_CONTEXTS = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> T parseObject(String xml, Class<T> type) {
    return parseObjectValidatingSchema(xml, type, null);
  }


  @SuppressWarnings("unchecked")
  public static <T> T parseObjectValidatingSchema(String xml, Class<T> type, Schema schema) {
    try {
      JAXBContext jaxbContext = getContext(type);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setSchema(schema);
      return (T) unmarshaller.unmarshal(new StringReader(xml));
    } catch (JAXBException e) {
      if (e.getCause() instanceof SAXParseException) {
        throw DslConfigurationException.INVALID_XML_INSTANCE_AGAINST_SCHEMA.withException(e.getCause());
      }
      throw new RuntimeException(e);
    }
  }


  public static String parseXml(Object object) {
    try {
      JAXBContext jaxbContext = getContext(object.getClass());
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

      StringWriter writer = new StringWriter();

      marshaller.marshal(object, writer);
      return writer.toString();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> JAXBContext getContext(Class<T> type)  {
    return JAXB_CONTEXTS.computeIfAbsent(type, k -> {
      try {
        return JAXBContext.newInstance(type);
      } catch (JAXBException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
