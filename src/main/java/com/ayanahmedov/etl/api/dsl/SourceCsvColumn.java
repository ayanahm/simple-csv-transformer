//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.03 at 04:34:38 PM EEST 
//


package com.ayanahmedov.etl.api.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SourceCsvColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SourceCsvColumn"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bindPatternPosition" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="sourceValueMapper" type="{http://www.ayanahmedov.com/CsvTransform}JavaClassName" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceCsvColumn", propOrder = {
    "name",
    "bindPatternPosition",
    "sourceValueMapper"
})
public class SourceCsvColumn {

    @XmlElement(required = true)
    protected String name;
    protected int bindPatternPosition;
    protected String sourceValueMapper;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the bindPatternPosition property.
     * 
     */
    public int getBindPatternPosition() {
        return bindPatternPosition;
    }

    /**
     * Sets the value of the bindPatternPosition property.
     * 
     */
    public void setBindPatternPosition(int value) {
        this.bindPatternPosition = value;
    }

    /**
     * Gets the value of the sourceValueMapper property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceValueMapper() {
        return sourceValueMapper;
    }

    /**
     * Sets the value of the sourceValueMapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceValueMapper(String value) {
        this.sourceValueMapper = value;
    }

    public SourceCsvColumn withName(String value) {
        setName(value);
        return this;
    }

    public SourceCsvColumn withBindPatternPosition(int value) {
        setBindPatternPosition(value);
        return this;
    }

    public SourceCsvColumn withSourceValueMapper(String value) {
        setSourceValueMapper(value);
        return this;
    }

}
