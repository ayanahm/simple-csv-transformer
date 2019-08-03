//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.03 at 02:02:00 PM EEST 
//


package com.ayanahmedov.etl.api.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 
 *                 camel casing as described in the take-home test PDF,
 *                 is not really necessary.
 *                 Since we map simply to the name we want at the end.
 *
 * 
 * <p>Java class for SourceTransformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SourceTransformation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="targetSchemaColumn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sourceBindPattern" type="{http://www.ayanahmedov.com/CsvTransform}SourceBindPattern"/&gt;
 *         &lt;element name="targetStringFormatter" type="{http://www.ayanahmedov.com/CsvTransform}TargetStringFormatter"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="sourceColumn" type="{http://www.ayanahmedov.com/CsvTransform}SourceCsvColumn" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceTransformation", propOrder = {
    "targetSchemaColumn",
    "sourceBindPattern",
    "targetStringFormatter",
    "sourceColumn"
})
public class SourceTransformation {

    @XmlElement(required = true)
    protected String targetSchemaColumn;
  @XmlElement(required = true)
  protected String sourceBindPattern;
    @XmlElement(required = true)
    protected TargetStringFormatter targetStringFormatter;
    protected List<SourceCsvColumn> sourceColumn;

    /**
     * Gets the value of the targetSchemaColumn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetSchemaColumn() {
        return targetSchemaColumn;
    }

    /**
     * Sets the value of the targetSchemaColumn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetSchemaColumn(String value) {
        this.targetSchemaColumn = value;
    }

  /**
   * Gets the value of the sourceBindPattern property.
   *
   * @return possible object is
   * {@link String }
   */
  public String getSourceBindPattern() {
    return sourceBindPattern;
  }

  /**
   * Sets the value of the sourceBindPattern property.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setSourceBindPattern(String value) {
    this.sourceBindPattern = value;
  }

    /**
     * Gets the value of the targetStringFormatter property.
     * 
     * @return
     *     possible object is
     *     {@link TargetStringFormatter }
     *     
     */
    public TargetStringFormatter getTargetStringFormatter() {
        return targetStringFormatter;
    }

    /**
     * Sets the value of the targetStringFormatter property.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetStringFormatter }
     *     
     */
    public void setTargetStringFormatter(TargetStringFormatter value) {
        this.targetStringFormatter = value;
    }

    /**
     * Gets the value of the sourceColumn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourceColumn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourceColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SourceCsvColumn }
     * 
     * 
     */
    public List<SourceCsvColumn> getSourceColumn() {
        if (sourceColumn == null) {
            sourceColumn = new ArrayList<SourceCsvColumn>();
        }
        return this.sourceColumn;
    }

    public SourceTransformation withTargetSchemaColumn(String value) {
        setTargetSchemaColumn(value);
        return this;
    }

    public SourceTransformation withSourceBindPattern(String value) {
      setSourceBindPattern(value);
      return this;
    }

  public SourceTransformation withTargetStringFormatter(TargetStringFormatter value) {
    setTargetStringFormatter(value);
    return this;
  }

  public SourceTransformation withSourceColumn(SourceCsvColumn... values) {
    if (values != null) {
      for (SourceCsvColumn value: values) {
                getSourceColumn().add(value);
            }
        }
        return this;
    }

    public SourceTransformation withSourceColumn(Collection<SourceCsvColumn> values) {
        if (values!= null) {
            getSourceColumn().addAll(values);
        }
        return this;
    }

}
