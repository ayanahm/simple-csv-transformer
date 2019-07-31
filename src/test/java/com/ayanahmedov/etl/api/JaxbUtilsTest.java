package com.ayanahmedov.etl.api;

import org.junit.jupiter.api.Test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaxbUtilsTest {

  @Test
  public void marshall_different_types() {
    String parsed1 = JaxbUtils.parseXml(new Type1("a", 1));
    String parsed2 = JaxbUtils.parseXml(new Type2(1, "a"));


    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<type1>\n" +
            "    <a>a</a>\n" +
            "    <b>1</b>\n" +
            "</type1>\n",
        parsed1);

    assertEquals(
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<type2>\n" +
            "    <a>1</a>\n" +
            "    <b>a</b>\n" +
            "</type2>\n",
        parsed2
    );
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement
  static class Type1 {
    private String a;
    private int b;

    public Type1() {
    }

    public Type1(String a, int b) {
      this.a = a;
      this.b = b;
    }

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }

    public int getB() {
      return b;
    }

    public void setB(int b) {
      this.b = b;
    }
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement
  static class Type2 {
    private int a;
    private String b;

    public Type2() {
    }

    public Type2(int a, String b) {
      this.a = a;
      this.b = b;
    }

    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }

    public String getB() {
      return b;
    }

    public void setB(String b) {
      this.b = b;
    }
  }

}