package contextproject.helpers;

import java.io.File;

public class FileName {
  /**
   * get the name of the folder.
   * 
   * @param path
   *          the absolute path of the folder.
   * @return the name of the folder.
   */
  public static String getName(String path) {
    String[] temp;
    String delimiter = File.separator;
    temp = path.split("\\" + delimiter);
    int size = temp.length;
    String name = temp[(size - 1)];
    return name.replaceAll(" ", "_").toLowerCase();
  }
}
