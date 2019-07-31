package com.ayanahmedov.etl.api;

public class DslConfigurationException extends RuntimeException {
  public static final DslConfigurationException
  WRONG_DATE_TIME_DSL = new DslConfigurationException(
      "Wrong DateTimeFormat pattern. Make sure to refer to java supported formats and sourceParsePattern in the dsl. ");

  public static final DslConfigurationException
      BIG_DECIMAL_CONSTRUCTOR_REQUIRES_PARAMETER_LOCALE = new DslConfigurationException(
      "BigDecimal constructor requires the parameter 'locale'");

  public static final DslConfigurationException
      STRING_FORMATTER_CONSTRUCTOR_REQUIRES_PARAMETER_FORMAT = new DslConfigurationException(
      "The parameter 'string-format', but be present but was not specified in the dsl.");

  public static final DslConfigurationException
      STRING_FORMATTER_INCORRECT = new DslConfigurationException(
      "String format provided is not correct." +
          "Make sure to only use %s placeholders, and no %d, %f etc." +
          "Since all values are passed as String.");

  public static final DslConfigurationException
      UNKNOWN_ELEMENT_CONSTRUCTOR_IN_DSL = new DslConfigurationException(
      "Unknown element constructor. Make sure configuration is correct. If so extend with new types as needed");

  public static final DslConfigurationException INVALID_XML_INSTANCE_EXECPTION = new DslConfigurationException(
      "Invalid DSL instance defined in XML which is violating the XSD validation.");;

  public DslConfigurationException(String message) {
    super(message);
    this.message = message;
  }

  private String message;

  public DslConfigurationException withException(Throwable e) {
    return new DslConfigurationException(message + "See exception message:" + e.getMessage() );
  }
}
