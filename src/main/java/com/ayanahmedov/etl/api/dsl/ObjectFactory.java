//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.31 at 01:58:25 PM CEST 
//


package com.ayanahmedov.etl.api.dsl;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ayanahmedov.etl.api.dsl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ayanahmedov.etl.api.dsl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CsvTransformationConfig }
     * 
     */
    public CsvTransformationConfig createCsvTransformationConfig() {
        return new CsvTransformationConfig();
    }

    /**
     * Create an instance of {@link SourceTransformation }
     * 
     */
    public SourceTransformation createSourceTransformation() {
        return new SourceTransformation();
    }

    /**
     * Create an instance of {@link CustomConstructor }
     * 
     */
    public CustomConstructor createCustomConstructor() {
        return new CustomConstructor();
    }

    /**
     * Create an instance of {@link ConstructorParameter }
     * 
     */
    public ConstructorParameter createConstructorParameter() {
        return new ConstructorParameter();
    }

    /**
     * Create an instance of {@link DateValueConstructor }
     * 
     */
    public DateValueConstructor createDateValueConstructor() {
        return new DateValueConstructor();
    }

    /**
     * Create an instance of {@link TargetDateFormat }
     * 
     */
    public TargetDateFormat createTargetDateFormat() {
        return new TargetDateFormat();
    }

    /**
     * Create an instance of {@link StringValueConstructor }
     * 
     */
    public StringValueConstructor createStringValueConstructor() {
        return new StringValueConstructor();
    }

    /**
     * Create an instance of {@link IntValueConstructor }
     * 
     */
    public IntValueConstructor createIntValueConstructor() {
        return new IntValueConstructor();
    }

    /**
     * Create an instance of {@link SourceCsvColumn }
     * 
     */
    public SourceCsvColumn createSourceCsvColumn() {
        return new SourceCsvColumn();
    }

    /**
     * Create an instance of {@link SourceConstantValue }
     * 
     */
    public SourceConstantValue createSourceConstantValue() {
        return new SourceConstantValue();
    }

    /**
     * Create an instance of {@link SourceCsv }
     * 
     */
    public SourceCsv createSourceCsv() {
        return new SourceCsv();
    }

    /**
     * Create an instance of {@link ElementConstructor }
     * 
     */
    public ElementConstructor createElementConstructor() {
        return new ElementConstructor();
    }

}
