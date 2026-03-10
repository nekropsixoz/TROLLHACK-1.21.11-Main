package lol.ethane.utils.misc;

import lombok.Generated;

public final class StringUtil {
   public static String normalizeEnumName(String string) {
      if (string.length() < 2) {
         return string;
      } else {
         StringBuilder result = new StringBuilder();
         boolean toUpperCase = false;

         for(int i = 0; i < string.length(); ++i) {
            char currentChar = string.charAt(i);
            if (currentChar == '_') {
               result.append(' ');
            } else if (currentChar == '$') {
               toUpperCase = true;
            } else if (toUpperCase) {
               result.append(Character.toUpperCase(currentChar));
               toUpperCase = false;
            } else {
               result.append(Character.toLowerCase(currentChar));
            }
         }

         if (!result.isEmpty()) {
            result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
         }

         return result.toString();
      }
   }

   @Generated
   private StringUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
