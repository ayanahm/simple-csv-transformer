package com.ayanahmedov.etl.api;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JaxbUtils {
  private static ConcurrentHashMap<Class<?>, JAXBContext> JAXB_CONTEXTS = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> T parseObject(String xml, Class<T> type) {
    try {
      JAXBContext jaxbContext = getContext(type);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      return (T) unmarshaller.unmarshal(new StringReader(xml));
    } catch (JAXBException e) {
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
