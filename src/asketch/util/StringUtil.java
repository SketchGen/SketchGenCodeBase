package asketch.util;

/**
 * This class provides string manipulation methods.
 */
public class StringUtil {

  /**
   * If the original string s contains the substring, return s.substring(i + 1) where i is the index
   * of the substring. Otherwise, return the original string s.  If last is false, i is the index of
   * the first occurrence of substring. Otherwise, i is the index of the last occurrence of
   * substring.
   */
  public static String afterSubstring(String s, String substring, boolean last) {
    int index;
    if (last) {
      index = s.lastIndexOf(substring);
    } else {
      index = s.indexOf(substring);
    }
    return index == -1 ? s : s.substring(index + 1);
  }

  /**
   * If the original string s contains the substring, return s.substring(0, i) where i is the index
   * of the substring. Otherwise, return the original string s.  If last is false, i is the index of
   * the first occurrence of substring. Otherwise, i is the index of the last occurrence of
   * substring.
   */
  public static String beforeSubstring(String s, String substring, boolean last) {
    int index;
    if (last) {
      index = s.lastIndexOf(substring);
    } else {
      index = s.indexOf(substring);
    }
    return index == -1 ? s : s.substring(0, index);
  }
}
