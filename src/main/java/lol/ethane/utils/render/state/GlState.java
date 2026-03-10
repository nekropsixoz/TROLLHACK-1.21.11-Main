package lol.ethane.utils.render.state;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;

public class GlState {
   private final int glVersion;
   private final GlProperties glProps;

   public GlState(int glVersion) {
      this.glVersion = glVersion;
      this.glProps = new GlProperties();
   }

   public final GlState push() {
      GL45.glGetIntegerv(34016, this.glProps.lastActiveTexture());
      GlStateManager._activeTexture(33984);
      GL45.glGetIntegerv(35725, this.glProps.lastProgram());
      GL45.glGetIntegerv(32873, this.glProps.lastTexture());
      if (this.glVersion >= 330 || GL.getCapabilities().GL_ARB_sampler_objects) {
         GL45.glGetIntegerv(35097, this.glProps.lastSampler());
      }

      GL45.glGetIntegerv(34964, this.glProps.lastArrayBuffer());
      GL45.glGetIntegerv(34229, this.glProps.lastVertexArrayObject());
      if (this.glVersion >= 200) {
         GL45.glGetIntegerv(2880, this.glProps.lastPolygonMode());
      }

      GL45.glGetIntegerv(2978, this.glProps.lastViewport());
      GL45.glGetIntegerv(3088, this.glProps.lastScissorBox());
      GL45.glGetIntegerv(32969, this.glProps.lastBlendSrcRgb());
      GL45.glGetIntegerv(32968, this.glProps.lastBlendDstRgb());
      GL45.glGetIntegerv(32971, this.glProps.lastBlendSrcAlpha());
      GL45.glGetIntegerv(32970, this.glProps.lastBlendDstAlpha());
      GL45.glGetIntegerv(32777, this.glProps.lastBlendEquationRgb());
      GL45.glGetIntegerv(34877, this.glProps.lastBlendEquationAlpha());
      this.glProps.lastEnableBlend(GL45.glIsEnabled(3042));
      this.glProps.lastEnableCullFace(GL45.glIsEnabled(2884));
      this.glProps.lastEnableDepthTest(GL45.glIsEnabled(2929));
      this.glProps.lastEnableStencilTest(GL45.glIsEnabled(2960));
      this.glProps.lastEnableScissorTest(GL45.glIsEnabled(3089));
      if (this.glVersion >= 310) {
         this.glProps.lastEnablePrimitiveRestart(GL45.glIsEnabled(36765));
      }

      this.glProps.lastDepthMask(GL45.glGetBoolean(2930));
      GL45.glGetIntegerv(35055, this.glProps.lastPixelUnpackBufferBinding());
      GlStateManager._glBindBuffer(35052, 0);
      GL45.glGetIntegerv(3328, this.glProps.lastPackSwapBytes());
      GL45.glGetIntegerv(3329, this.glProps.lastPackLsbFirst());
      GL45.glGetIntegerv(3330, this.glProps.lastPackRowLength());
      GL45.glGetIntegerv(3332, this.glProps.lastPackSkipPixels());
      GL45.glGetIntegerv(3331, this.glProps.lastPackSkipRows());
      GL45.glGetIntegerv(3333, this.glProps.lastPackAlignment());
      GL45.glGetIntegerv(3312, this.glProps.lastUnpackSwapBytes());
      GL45.glGetIntegerv(3313, this.glProps.lastUnpackLsbFirst());
      GL45.glGetIntegerv(3317, this.glProps.lastUnpackAlignment());
      GL45.glGetIntegerv(3314, this.glProps.lastUnpackRowLength());
      GL45.glGetIntegerv(3316, this.glProps.lastUnpackSkipPixels());
      GL45.glGetIntegerv(3315, this.glProps.lastUnpackSkipRows());
      if (this.glVersion >= 120) {
         GL45.glGetIntegerv(32876, this.glProps.lastPackImageHeight());
         GL45.glGetIntegerv(32875, this.glProps.lastPackSkipImages());
         GL45.glGetIntegerv(32878, this.glProps.lastUnpackImageHeight());
         GL45.glGetIntegerv(32877, this.glProps.lastUnpackSkipImages());
      }

      GlStateManager._pixelStore(3317, 1);
      GlStateManager._pixelStore(3314, 0);
      GlStateManager._pixelStore(3316, 0);
      GlStateManager._pixelStore(3315, 0);
      return this;
   }

   public final GlState pop() {
      GlStateManager._glUseProgram(this.glProps.lastProgram()[0]);
      GlStateManager._bindTexture(this.glProps.lastTexture()[0]);
      if (this.glVersion >= 330 || GL.getCapabilities().GL_ARB_sampler_objects) {
         GL45.glBindSampler(0, this.glProps.lastSampler()[0]);
      }

      GlStateManager._activeTexture(this.glProps.lastActiveTexture()[0]);
      GlStateManager._glBindVertexArray(this.glProps.lastVertexArrayObject()[0]);
      GlStateManager._glBindBuffer(34962, this.glProps.lastArrayBuffer()[0]);
      GL45.glBlendEquationSeparate(this.glProps.lastBlendEquationRgb()[0], this.glProps.lastBlendEquationAlpha()[0]);
      GlStateManager._blendFuncSeparate(this.glProps.lastBlendSrcRgb()[0], this.glProps.lastBlendDstRgb()[0], this.glProps.lastBlendSrcAlpha()[0], this.glProps.lastBlendDstAlpha()[0]);
      if (this.glProps.lastEnableBlend()) {
         GlStateManager._enableBlend();
      } else {
         GlStateManager._disableBlend();
      }

      if (this.glProps.lastEnableCullFace()) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }

      if (this.glProps.lastEnableDepthTest()) {
         GlStateManager._enableDepthTest();
      } else {
         GlStateManager._disableDepthTest();
      }

      if (this.glProps.lastEnableStencilTest()) {
         GL45.glEnable(2960);
      } else {
         GL45.glDisable(2960);
      }

      if (this.glProps.lastEnableScissorTest()) {
         GlStateManager._enableScissorTest();
      } else {
         GlStateManager._disableScissorTest();
      }

      if (this.glVersion >= 310) {
         if (this.glProps.lastEnablePrimitiveRestart()) {
            GL45.glEnable(36765);
         } else {
            GL45.glDisable(36765);
         }
      }

      if (this.glVersion >= 200) {
         GlStateManager._polygonMode(1032, this.glProps.lastPolygonMode()[0]);
      }

      GlStateManager._viewport(this.glProps.lastViewport()[0], this.glProps.lastViewport()[1], this.glProps.lastViewport()[2], this.glProps.lastViewport()[3]);
      GlStateManager._scissorBox(this.glProps.lastScissorBox()[0], this.glProps.lastScissorBox()[1], this.glProps.lastScissorBox()[2], this.glProps.lastScissorBox()[3]);
      GlStateManager._pixelStore(3328, this.glProps.lastPackSwapBytes()[0]);
      GlStateManager._pixelStore(3329, this.glProps.lastPackLsbFirst()[0]);
      GlStateManager._pixelStore(3330, this.glProps.lastPackRowLength()[0]);
      GlStateManager._pixelStore(3332, this.glProps.lastPackSkipPixels()[0]);
      GlStateManager._pixelStore(3331, this.glProps.lastPackSkipRows()[0]);
      GlStateManager._pixelStore(3333, this.glProps.lastPackAlignment()[0]);
      GlStateManager._glBindBuffer(35052, this.glProps.lastPixelUnpackBufferBinding()[0]);
      GlStateManager._pixelStore(3312, this.glProps.lastUnpackSwapBytes()[0]);
      GlStateManager._pixelStore(3313, this.glProps.lastUnpackLsbFirst()[0]);
      GlStateManager._pixelStore(3317, this.glProps.lastUnpackAlignment()[0]);
      GlStateManager._pixelStore(3314, this.glProps.lastUnpackRowLength()[0]);
      GlStateManager._pixelStore(3316, this.glProps.lastUnpackSkipPixels()[0]);
      GlStateManager._pixelStore(3315, this.glProps.lastUnpackSkipRows()[0]);
      if (this.glVersion >= 120) {
         GlStateManager._pixelStore(32876, this.glProps.lastPackImageHeight()[0]);
         GlStateManager._pixelStore(32875, this.glProps.lastPackSkipImages()[0]);
         GlStateManager._pixelStore(32878, this.glProps.lastUnpackImageHeight()[0]);
         GlStateManager._pixelStore(32877, this.glProps.lastUnpackSkipImages()[0]);
      }

      GlStateManager._depthMask(this.glProps.lastDepthMask());
      return this;
   }
}
