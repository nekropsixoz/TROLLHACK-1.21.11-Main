package lol.aether.font;

import lol.aether.font.msdf.MsdfFont;
import lombok.Generated;
import net.minecraft.class_310;

public class MgfxFont {
   private final String name;
   private final float size;
   private MsdfFont font;

   public MgfxFont(String name, float size) {
      this.name = name;
      this.size = size;
   }
   
   private void ensureLoaded() {
      if (this.font == null) {
         String fileName = this.name.toLowerCase().replace(" ", "_");
         class_310 mc = class_310.method_1551();
         if (mc != null && mc.method_1478() != null) {
            this.font = MsdfFont.create(mc.method_1478(), this.name, fileName, fileName);
         }
      }
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public float getSize() {
      return this.size;
   }

   @Generated
   public MsdfFont getFont() {
      ensureLoaded();
      return this.font;
   }
}
