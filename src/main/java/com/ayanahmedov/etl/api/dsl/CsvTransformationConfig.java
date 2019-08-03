//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.03 at 02:02:00 PM EEST 
//


package com.ayanahmedov.etl.api.dsl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transformation" type="{http://www.ayanahmedov.com/CsvTransform}SourceTransformation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transformation"
})
@XmlRootElement(name = "csv-transformation-config")
public class CsvTransformationConfig {

    protected List<SourceTransformation> transformation;

    /**
     * Gets the value of the transformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SourceTransformation }
     * 
     * 
     */
    public List<SourceTransformation> getTransformation() {
        if (transformation == null) {
            transformation = new ArrayList<SourceTransformation>();
        }
        return this.transformation;
    }

    public CsvTransformationConfig withTransformation(SourceTransformation... values) {
        if (values!= null) {
            for (SourceTransformation value: values) {
                getTransformation().add(value);
            }
        }
        return this;
    }

    public CsvTransformationConfig withTransformation(Collection<SourceTransformation> values) {
        if (values!= null) {
            getTransformation().addAll(values);
        }
        return this;
    }

}
