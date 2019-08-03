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


/**
 * <p>Java class for DateValueFormatter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DateValueFormatter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sourceFormatPattern" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="targetDateFormat" type="{http://www.ayanahmedov.com/CsvTransform}TargetDateFormat"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DateValueFormatter", propOrder = {
    "sourceFormatPattern",
    "targetDateFormat"
})
public class DateValueFormatter {

    @XmlElement(required = true)
    protected String sourceFormatPattern;
    @XmlElement(required = true)
    protected TargetDateFormat targetDateFormat;

    /**
     * Gets the value of the sourceFormatPattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceFormatPattern() {
        return sourceFormatPattern;
    }

    /**
     * Sets the value of the sourceFormatPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceFormatPattern(String value) {
        this.sourceFormatPattern = value;
    }

    /**
     * Gets the value of the targetDateFormat property.
     * 
     * @return
     *     possible object is
     *     {@link TargetDateFormat }
     *     
     */
    public TargetDateFormat getTargetDateFormat() {
        return targetDateFormat;
    }

    /**
     * Sets the value of the targetDateFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetDateFormat }
     *     
     */
    public void setTargetDateFormat(TargetDateFormat value) {
        this.targetDateFormat = value;
    }

  public DateValueFormatter withSourceFormatPattern(String value) {
        setSourceFormatPattern(value);
        return this;
    }

  public DateValueFormatter withTargetDateFormat(TargetDateFormat value) {
        setTargetDateFormat(value);
        return this;
    }

}