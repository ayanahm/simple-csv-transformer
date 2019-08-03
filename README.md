##### Table of Contents  
* [Overview](#overview)
* [Java-Api](#api-usage)
   * [The Concept] (#the-concept)
* [Dsl](#dsl)
* [Further Work](#further-work)      


# Overview

This repo, provides a simple CSV transformer, which can be configured by a an external DSL based on validatable XML,
which produces the target CSV file.

There is an exception to the CSV standards, it assumes that there must be always a header row present in the source CSV file.
If not then manually needs to be corrected.


# Api Usage<a name="api-usage"></a>

A basic usage in Java code looks like the following:

```java

  void writeToFile(Path sourceCsv, Path outputCsv, Path configFile) throws IOException {
      CsvTransformer transformer = new CsvTransformerBuilder()
          .withXmlDsl(configFile)
          .build();
  
      try (BufferedReader csvReader = Files.newBufferedReader(sourceCsv);
           BufferedWriter csvWriter = Files.newBufferedWriter(outputCsv)) {
  
  
        transformer.transform(csvReader, csvWriter);
      }
    }
```

**Note:** The CsvTransformer does not handle the lifecycle of the buffers at all.
It is up to client to close the streams and flush when needed 
(Note the example above will close+flush when try-with-resource completes.)

It is also up to client, to use correct streams(BufferedReader, BufferedWriter for file system accessing CSV files).

## The Concept<a name="the-concept"></a> 

The concept of the mapping from `source-file` -> `target-file` is as follows:

Each row ,except the header, in the source CSV is applied by three step modification.

`Mapper -> Reducer -> Formatter`

For each row, and for each column, the `Mapper` is applied.

Then `Reducer` reduces the mapped column values into a single `String` value.
Which may `reduce` more then 1 mapped value.
 
The reduced `String` value is then applied to `Formatter`. 
The output of the `Formatter` 
is then written into the output CSV file.

 `Mapper` and `Formatter` are extensible by providing custom implementations. 
 For the simplicity reasons, the reducer is kept with only 1 implementation. But open for later extensions.



There are some other examples of the usage, which are also happen to be the actual test suite used for the library.
Those can be found under:
https://github.com/ayanahm/demo-etl/tree/master/src/test/java/com/ayanahmedov/etl/api

This test shows basic CSVTransformer usage.
Note , that test are using temporary files which are deleted on exit. 

https://github.com/ayanahm/demo-etl/blob/master/src/test/java/com/ayanahmedov/etl/api/SimpleCsvTransformerTest.java

Ax example of introducing a custom formatter.
https://github.com/ayanahm/demo-etl/blob/master/src/test/java/com/ayanahmedov/etl/api/SumColumnsTest.java

DSL configurations for those tests can be viewed here:
https://github.com/ayanahm/demo-etl/tree/master/src/test/resources 




# DSL<a name="dsl"></a>

In most basic form the DSL looks like the following:

```
<?xml version="1.0" encoding="utf-8"?>
<csv-transformation-config xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns="http://www.ayanahmedov.com/CsvTransform"
                           xsi:schemaLocation="http://www.ayanahmedov.com/CsvTransform csv-transform-schema.xsd">

    <transformation>
        <targetSchemaColumn>formatted</targetSchemaColumn>
        <sourceBindPattern>$1,$2,$1</sourceBindPattern>
        <targetStringFormatter>
            <formatterClass>
                <className>com.ayanahmedov.etl.api.tostringformatter.FormattingToStringFormatter</className>
                <parameter>
                    <name>string-format</name>
                    <value>%5s:%s.  %s</value>
                </parameter>
            </formatterClass>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <bindPatternPosition>1</bindPatternPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-2</name>
            <bindPatternPosition>2</bindPatternPosition>
        </sourceColumn>
    </transformation>
</csv-transformation-config>
```

Next will be explained the specific parts in the DSL.
Note that, valid DSL is defined by an XSD, which can be analyzed more in detail in [xsd file](https://github.com/ayanahm/demo-etl/blob/master/src/main/resources/csv-transform-schema.xsd).

##  `transformation`

This is the main entry point. Specifies how to transform the source CSV row to the target CSV row.
There can be unlimited number of such transformations defined. Each corresponds to a  `Mapper->Reducer->Formatter` flow.

### `transformation/targetSchemaColumn`

Defines the name of the target CSV column will be populated.

### `transformation/targetStringFormatter`

The library comes with some built-in formatters. 
The list of built-in mappers which are declaratively  possible defined in the type `ElementConstructor`.
The xsd defines the following ones:

`DateValueFormatter`, `StringValueFormatter` and `IntValueFormatter`
are built in CSV value formatters. But there are also implemented custom formatters present in the package
`com.ayanahmedov.etl.api.tostringformatter`.

### `transformation/sourceColumn`
Defines which rows to reduce.  The number of `sourceColumns` are unbounded. 
Also note it is allowed to not to provide any.
That makes sense, when the target csv needs to be populated 
with a constant value using the `sourceBindPattern` without any placeholder.

### `transformation/sourceColumn/sourceValueMapper`
Contains classname of a `Mapper`. The source CSV value
is read and supplied into this provided implementation.

### `transformation/sourceBindPattern`
This defines a *template* for the reducer. Currently there is only an implementation for
placeholders using dollar sign as in `$1-$2`. 

The place holders will be replaced by the value from the CSV value *after* applying the mapper, if there is any mapper.

Imagine a CSV with following values:

```
Col-1,Col-2
a,b
b,c
d,c
```

And a snippet from the DSL instance:
```
    <transformation>
        <targetSchemaColumn>Col-X</targetSchemaColumn>
        <sourceBindPattern>[$1,$2]</sourceBindPattern>
        <targetStringFormatter>
            <stringValueFormatter/>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <bindPatternPosition>2</bindPatternPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-2</name>
            <bindPatternPosition>1</bindPatternPosition>
        </sourceColumn>
    </transformation>
```

The output CSV will contain the following:

```
"Col-X"
"[b,a]"
"[c,b]"
"[c,d]"
```

Note the **bindPatternPosition** which defines, how the source column is mean to be replaced in
*sourceBindPattern*.  

The `sourceBindPattern` must not necessarily contain a placeholder. This is useful when there are no
source columns to map from. So a constant value can be mapped.
See for example:
```
<transformation>
        <targetSchemaColumn>Col-X</targetSchemaColumn>
        <sourceBindPattern>kg</sourceBindPattern>
        <targetStringFormatter>
            <stringValueFormatter/>
        </targetStringFormatter>
    </transformation>
```

## Built-In constructors:

### StringValueFormatter
This formatter requires no parameter, and simply returns the reduced string value.
Useful when no explicit formatting required.

see snippet
```xml
        <targetStringFormatter>
            <stringValueFormatter/>
        </targetStringFormatter>
```

### IntValueFormatter
Trims the reduced value, and validates the value against integer value.

```xml
        <targetStringFormatter>
            <intValueFormatter/>
        </targetStringFormatter>
```
### BigDecimalValueFormatter
Parses reduced value by a locale
and prints optionally with a locale.
Requires parameters:

`source-locale` the locale to use when parsing the big decimal
`target-locale` optional. the locale to use when printing the parsed big decimal
If not provided, default .toString implementation is used which will print in scientific notation.

Snippet:
```xml
    <transformation>
        <targetSchemaColumn>locale_DE</targetSchemaColumn>
        <sourceBindPattern>$1</sourceBindPattern>
        <targetStringFormatter>
            <bigDecimalValueFormatter>
                <source-locale>en-US</source-locale>
                <target-locale>de-DE</target-locale>
            </bigDecimalValueFormatter>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <bindPatternPosition>1</bindPatternPosition>
        </sourceColumn>
    </transformation>
```

 
### DateTimeFormatter
Formats reduced value into the requested format.
When parsing the reduced value, it defaults hard-coded defaults when missing.
Example: 2018-01-01, when requested to be parsed to an instant
 2018-01-01:00:00 is used, using `targetDateFormat/zoneId` or system default if that is not present.
 
 `sourceFormatPattern` : defines how to parse the reduced value. Note that
 not all possible java patterns are possible. For details please refer to javadoc of: com.ayanahmedov.etl.api.tostringformatter.DateByDateTimePatternFormatter
 
 `targetDateFormat/formatPattern`: defines the pattern in the target output csv.
 `targetDateFormat/zoneId`: the target zoneId to use when formatting into string. In case not provided, System default is used.

```xml
    <transformation>
        <targetSchemaColumn>yyyy-MM-dd</targetSchemaColumn>
        <sourceBindPattern>$1-$2-$3</sourceBindPattern>
        <targetStringFormatter>
            <dateValueFormatter>
                <sourceFormatPattern>yyyy-MM-dd</sourceFormatPattern>
                <targetDateFormat>
                    <formatPattern>yyyy:LL:dd</formatPattern>
                    <zoneId>Europe/Vienna</zoneId>
                </targetDateFormat>
            </dateValueFormatter>
        </targetStringFormatter>
        <sourceColumn>
            <name>Col-1</name>
            <bindPatternPosition>1</bindPatternPosition>
        </sourceColumn>
        <sourceColumn>
            <name>Col-2</name>
            <bindPatternPosition>2</bindPatternPosition>
            <sourceValueMapper>com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer</sourceValueMapper>
        </sourceColumn>
        <sourceColumn>
            <name>Col-3</name>
            <bindPatternPosition>3</bindPatternPosition>
            <sourceValueMapper>com.ayanahmedov.etl.api.sourcemapper.TwoDigitsNormalizer</sourceValueMapper>
        </sourceColumn>
    </transformation>

```


See also the usage of `source/column/sourceValueMapper` using a `TwoDigitsNormalizer`
which is converting values like `1` to `01` , so they are parsable by the
provided date time format.


# Further Work<a name="further-work"></a>

## Alternative DSL sources

Currently only DSL via XML is supported out of the box.
Even though XML can be validated via an XSD, and mostly supported very well in Java world. 
Also would be friendly for the tools to generate it(i.e a WebPage where the tranformation files are created via a GUI), 
it is  not strictly necessary. 
Client could get to chose to provide an DSL provider, 
simply by including into the runtime.
This can be made possible to define only a DSL dependency and ask for clients to provide runtime implementations,
using standard `maven` | `gradle` functionalities.

## More features

More built in features can be introduced. Also a Reducer strategy can be implemented.
The current way only supports mapping between string values. It might be useful to enable access to
parsed java objects.

## Optimizations

Experimental idea:
In most of the up-to-date operating systems, it should be possible to introduce the speed significantly for huge csv files.
With the idea being, the some chunks of rows can be transformed in parallel. Where each job working on a single chunk,
can write into a temporary file. Using FileChannel#transferFrom, the files can be joined into the final CSV file. 
The point in this is the transferFrom delegates to operating system, and modern systems use memory caches for actively used files which is significantly faster. 
Hence ,though experimental, would worth trying.