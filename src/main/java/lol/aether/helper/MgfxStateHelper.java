package lol.aether.helper;

import com.mojang.blaze3d.opengl.GlStateManager;

public class MgfxStateHelper implements AutoCloseable {
   private final int unitIndex;

   public static MgfxStateHelper capture(int restoreUnitIndex) {
      return new MgfxStateHelper(restoreUnitIndex);
   }

   private MgfxStateHelper(int restoreUnitIndex) {
      this.unitIndex = restoreUnitIndex;
   }

   public void close() {
      GlStateManager._activeTexture('蓀' + this.unitIndex);
      GlStateManager._bindTexture(0);
      GlStateManager._activeTexture(33984);
      GlStateManager._glBindVertexArray(0);
      GlStateManager._glBindBuffer(34962, 0);
      GlStateManager._glUseProgram(0);
      GlStateManager._enableBlend();
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      GlStateManager._enableDepthTest();
      GlStateManager._depthMask(true);
      GlStateManager._disableScissorTest();
   }
}
