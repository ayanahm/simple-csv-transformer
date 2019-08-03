package com.ayanahmedov.etl.api.sourcemapper;

public class LeadingAndTrailingWhiteSpaceTrimmer implements SourceColumnMapper {
  private static final LeadingAndTrailingWhiteSpaceTrimmer instance = new LeadingAndTrailingWhiteSpaceTrimmer();

  private LeadingAndTrailingWhiteSpaceTrimmer() {
  }

  public static LeadingAndTrailingWhiteSpaceTrimmer get() {
    return instance;
  }

  @Override
  public String map(String csvSourceValue) {
    return csvSourceValue.trim();
  }
}
