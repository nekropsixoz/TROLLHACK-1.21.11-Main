package lol.aether.font;

import java.util.concurrent.ConcurrentHashMap;

public class MgfxFontRepository {
   private static final ConcurrentHashMap<String, MgfxFont> fonts = new ConcurrentHashMap();

   public static MgfxFont fetch(String name, float size) {
      String key = name + ":" + size;
      return (MgfxFont)fonts.computeIfAbsent(key, (k) -> {
         return new MgfxFont(name, size);
      });
   }
}
