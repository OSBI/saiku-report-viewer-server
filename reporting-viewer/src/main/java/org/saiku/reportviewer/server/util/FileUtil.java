package org.saiku.reportviewer.server.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A class containing utility methods to handle files.
 */
public class FileUtil {
  private static final String DEFAULT_PREFIX = "report";
  private static final String FORMAT_SEPARATOR = ".";
  private static final int BUFFER_SIZE = 1024;

  public static File createTempFile(String format) throws IOException {
    return File.createTempFile(DEFAULT_PREFIX, FORMAT_SEPARATOR + format);
  }

  public static void copy(InputStream input, OutputStream output) throws  IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    int read;

    while((read = input.read(bytes)) > 0) {
      output.write(bytes, 0, read);
    }

    input.close();
    output.flush();
    output.close();
  }

  public static List<String> listFileNames(File root) {
    List<String> result = new ArrayList<>();

    for (File f : root.listFiles()) {
      result.add(f.getName());
    }

    return result;
  }
}
