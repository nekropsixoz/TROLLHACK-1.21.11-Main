package lol.aether.helper;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_10868;

public class MgfxTextureHelper {
   public static int getTextureId(GpuTexture texture) {
      if (texture instanceof class_10868) {
         class_10868 glTexture = (class_10868)texture;
         return glTexture.method_68427();
      } else {
         return 0;
      }
   }
}
