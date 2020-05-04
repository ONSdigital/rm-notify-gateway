package uk.gov.ons.ctp.response.notify.lib.common;

/** Class to collect together some useful string manipulation methods */
public class StringUtils {

  /**
   * Take a string and split into a number of equally sized segments
   *
   * @param text the string to split
   * @param size the segment size
   * @return the array of segments
   */
  public static String[] splitEqually(String text, int size) {
    String[] ret = new String[text.length() / size];

    for (int start = 0, segment = 0; start < text.length(); start += size, segment++) {
      ret[segment] = (text.substring(start, Math.min(text.length(), start + size)));
    }
    return ret;
  }

  /**
   * Calculate the ordinal position of a char in a char array
   *
   * @param arr the array
   * @param c the char
   * @return the numeric index of that char in the array
   */
  public static int indexOf(char[] arr, char c) {
    int ret = -1;
    if (arr != null) {
      for (int n = 0; n < arr.length; n++) {
        if (arr[n] == c) {
          ret = n;
          break;
        }
      }
    }
    return ret;
  }
}
