##### Table of Contents  
[Headers](#overview)
  
[Java-Api](#api-usage)

[Dsl](#dsl)

[Improvements](#improvements)      


# Overview

This repo, provides a simple CSV transformer, which can be configured by a DSL based on validatable XML,
to a target CSV file.
In a summary, this provides a glorified function which can be declared as following:

from `source-csv-file.csv` -> `target-csv-file.csv`.

# Api Usage

There are examples for the usage, which are also happen to be the actual test suite used for the library.
Those can be found under:
https://github.com/ayanahm/demo-etl/tree/master/src/test/java/com/ayanahmedov/etl/api

which are also including DSL configurations for those tests:
https://github.com/ayanahm/demo-etl/tree/master/src/test/resources 

In basic form it looks like this:

```java
    Path config = ...path to xml-dsl instance...
    Path csv = .. path to csv to transform ...
    Path output = .. path to target csv file ... 

    CsvTransformer transformer = new CsvTransformerBuilder()
        .withXmlDsl(config)
        .build();

     try (
            BufferedWriter writer = Files.newBufferedWriter(outputTemp);
            BufferedReader reader = Files.newBufferedReader(csv)) {
    
          transformer.transform(reader, writer);
        }

    
```

Api is meant to be kept minimalistic.
As a result, note that,
The CsvTransformer does not handle the lifecycle of the buffers at all.
It is up to client to close the streams and flush when needed 
(note the example above will close+flush on try-with-resource block finish.)

It is also up to client, to use correct streams(BufferedReader, BufferedWriter for FileSystem accessing streams).


# DSL

In most basic form the DSL looks like the following:

```
<?xml version="1.0" encoding="utf-8"?>
<csv-transformation-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns="http://www.ayanahmedov.com/CsvTransform"
                           xsi:schemaLocation="http://www.ayanahmedov.com/CsvTransform csv-transform-schema.xsd">

    <transformation>
        <targetSchemaColumn>average</targetSchemaColumn>
        <targetStringFormatter>
            <sourceBindPattern>$1,$2</sourceBindPattern>
            <constructorClass>
                <className>com.ayanahmedov.etl.api.tostringformatter.ColumnAverageFormatter</className>
            </constructorClass>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-3</name>
            <constructorPosition>1</constructorPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-4</name>
            <constructorPosition>2</constructorPosition>
        </sourceColumn>
    </transformation>
</csv-transformation-config>
```

Next will be explained the specific parts in the DSL.
Note that, valid DSL is defined by an XSD, which can be analyzed more in detail in [xsd file](https://github.com/ayanahm/demo-etl/blob/master/src/main/resources/csv-transform-schema.xsd).

##  `transformation`


This is the main entry point. Specifies how to transform the source CSV row to the target CSV row.

## `targetSchemaColumn`

defines the name of the target CSV column will be populated.

# `targetStringFormatter`

The library comes with some built-in mappers. 
The list of built-in mappers which are declaratively  possible defined in the type `ElementConstructor`.
The xsd defines the following ones:

```$xml
    <xs:complexType name="ElementConstructor">
        <xs:sequence>
            <xs:element type="SourceBindPattern" name="sourceBindPattern">
                <xs:annotation>
                    <xs:documentation>
                        Indicates the pattern, variables are interpolated by $1, $2 etc.
                        The final string value is used for the actual creation of the target object.
                    </xs:documentation>
                </xs:annotation>
            </xs:element>
            <xs:choice>
                <xs:element name="constructorClass" type="CustomConstructor">
                    <xs:annotation>
                        <xs:documentation>custom value constructor</xs:documentation>
                    </xs:annotation>
                </xs:element>

                <!--                build in formatters-->
                <xs:element name="dateValueConstructor" type="DateValueConstructor"/>
                <xs:element name="stringValueConstructor" type="StringValueConstructor"/>
                <xs:element name="intValueConstructor" type="IntValueConstructor"/>
                <!--            extend as needed. and also make sure to extend backing java.-->
            </xs:choice>
        </xs:sequence>
    </xs:complexType>
```
 
As can be seen, `DateValueConstructor`, `StringValueConstructor` and `IntValueConstructor`
are build in CSV value formatters.

## `sourceBindPattern`

This is how the source CSV value or multiple CSV values are bound together and passed into the formatters.

Best would be to demonstrate with an example:

```$xml
    <transformation>
        <targetSchemaColumn>Date</targetSchemaColumn>
        <targetStringFormatter>
            <sourceBindPattern>$1-$2-$3</sourceBindPattern>
            <dateValueConstructor>
                <sourceFormatPattern>yyyy-MM-dd</sourceFormatPattern>
                <targetDateFormat>
                    <formatPattern>yyyy:LL:dd</formatPattern>
                    <zoneId>Europe/Vienna</zoneId>
                </targetDateFormat>
            </dateValueConstructor>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <constructorPosition>1</constructorPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-2</name>
            <constructorPosition>2</constructorPosition>
            <sourceValueMapper>com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer</sourceValueMapper>
        </sourceColumn>
        <sourceColumn>
            <name>Col-3</name>
            <constructorPosition>3</constructorPosition>
            <sourceValueMapper>com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer</sourceValueMapper>
        </sourceColumn>
    </transformation>
``` 

In this sample,
the target CSV will be created with a column named `Date` 
and the value inside each column will be mapped from the source CSV,
via the `sourceBindPattern` interpolated with `$` signed placeholders.


for each of the source CSV record, 
* the value of which is under the header column `Col-1` will bind itself into placeholder `$1`
* the value of which is under the header column `Col-2` will bind itself into placeholder `$2` after performing `sourceValueMapper`
* the value of which is under the header column `Col-3` will bind itself into placeholder `$3` after performing `sourceValueMapper`

Imagine a CSV with following values:

```$xslt
    Col-1,Col2,Col3
    2019,7,31
    2019,8,1
```

The mentioned DSL will first transform `Col-2` and `Col-3` by a customer `sourceValueMapper`
which happens to normalize two digits to be friendly with DateTimeFormatter.

After normalizing the month values `7` -> `07` and `8` -> `08`, also for `Col-3`, 
normalizing `1` -> `01`,
the effective value of the bind value will be for the rows:
* row-1: `2019-07-31`
* row-2: `2019-08-1`

Note the dashes are preserved, simply because, 
the pattern can define any constant, only replaced values are those identified by `$<position>`

Note also that, `sourceColumn` may be multiple columns, and even not, 
they must all point to the  placeholder they are meant to be mapped.

Forcing to provide constructor positions and not allowing default semantics is intentional. 
With the idea to prevent unnecessary complexity both on DSL level and in Java level.


## Custom implementation of `MappedCsvValueStringConstructor`

In case the formatters embedded in DSL is not enough(probably most of the time not),
then library is allowing the implementations of `MappedCsvValueStringConstructor`  to be registered
and then configured in the DSL.

Some such plugins are within the library also present. An example is: https://github.com/ayanahm/demo-etl/blob/master/src/main/java/com/ayanahmedov/etl/api/objectconstructor/FormattingStringConstructor.java
Such plugins can be embedded like this:


```$xslt
<?xml version="1.0" encoding="utf-8"?>
<csv-transformation-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns="http://www.ayanahmedov.com/CsvTransform"
                           xsi:schemaLocation="http://www.ayanahmedov.com/CsvTransform csv-transform-schema.xsd">

    <transformation>
        <targetSchemaColumn>formatted</targetSchemaColumn>
        <targetStringFormatter>
            <sourceBindPattern>$1,$2,$1</sourceBindPattern>
            <constructorClass>
                <className>com.ayanahmedov.etl.api.tostringformatter.FormattingToStringFormatter</className>
                <parameter>
                    <name>string-format</name>
                    <value>%5s:%s.  %s</value>
                </parameter>
            </constructorClass>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <constructorPosition>1</constructorPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-2</name>
            <constructorPosition>2</constructorPosition>
        </sourceColumn>
    </transformation>
</csv-transformation-config>
```

This example also demonstres, 
the `sourceBindPattern` can reference multiple source CSV columns.

*  `Col-1` is replaced in the pattern `$1,$2,$1` twice.
*  `Col-2` is replaced in the pattern `$1,$2,$1` in the middle.

Custom mappers can also have parameters defined in the DSL.
Which is visible in this case, which is providing a list of `parameter` objects.
Those parameters are passed as usual `java.util.Map` on source CSV row map time.
see also: https://github.com/ayanahm/demo-etl/blob/master/src/main/java/com/ayanahmedov/etl/api/objectconstructor/MappedCsvValueStringConstructor.java

# improvements

## Alternative DSL sources

Currently only DSL via XML is supported out of the box.
Even though XML can be validated via an XSD, and mostly supported very well in Java world. 
Also easy use tools to generated it(i.e a WebPage where the tranformation files are created via a GUI), 
it is  not strictly necessary. 
Client could get to chose to provide an DSL provider, 
simply by including into the runtime.
This can be made possible to define only a DSL dependency and ask for clients to provide runtime implementations,
using standard `maven` | `gradle` functionalities.

## More flexibility

The library is opinionated. 
More extensions can be enabled, per source CSV row basic,
intermediate calculations and what more.

## GC tuning

running https://github.com/ayanahm/demo-etl/blob/master/src/test/java/com/ayanahmedov/etl/api/BigCsvFilesTest.java
with VM arguments `-gc:verbose -XX:+PrintGCDetails -Xmx32m`
show that GC is struggling with 10megabyte of data to be transformed. Due to intermediate objects created
during the mapping of individual CSV rows. Which can be mostly mitigated by optimizing the implementation
to cache the instance.