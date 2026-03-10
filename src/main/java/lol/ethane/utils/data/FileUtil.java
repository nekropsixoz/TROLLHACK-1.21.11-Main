package lol.ethane.utils.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileUtil {
   public static String readFile(File file) throws IOException {
      try {
         if (file.exists() && file.isFile()) {
            InputStream is = Files.newInputStream(file.toPath());
            StringBuilder stringBuilder = new StringBuilder();

            int b;
            while((b = is.read()) != -1) {
               stringBuilder.append((char)b);
            }

            is.close();
            return stringBuilder.toString();
         } else {
            return null;
         }
      } catch (IOException e) {
         throw e;
      }
   }
}
