package uk.gov.ons.ctp.response.notify.util;

import uk.gov.ons.ctp.common.util.StringUtils;

/**
 * Class to centralise the formatting of IAC codes
 */
public class InternetAccessCodeFormatter {

  private static final int SEGMENT_SIZE = 4;
  private static final String SEGMENT_SEPARATOR = " ";

  /**
   * Take an IAC code, insert separators between segments and transform all to uppercase.
   *
   * @param iac the internalized IAC code ie "2345bcde6789"
   * @return the IAC with separators inserted ie "2345 BCDE 6789"
   */
  public static final String externalize(String iac) {
    String[] segments = StringUtils.splitEqually(iac, SEGMENT_SIZE);
    return org.springframework.util.StringUtils.arrayToDelimitedString(segments, SEGMENT_SEPARATOR).toUpperCase();
  }
}
