package lol.aether.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.Texture;
import lol.aether.renderer.batch.MgfxFontRenderer;
import lol.aether.renderer.batch.MgfxRectangleRenderer;
import lol.aether.renderer.repository.MgfxRendererRepository;
import lol.aether.renderer.sandbox.MgfxSandboxRenderer;
import lombok.Generated;
import net.minecraft.class_310;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

public class MgfxContext {
   private final Matrix4fStack matrixStack = new Matrix4fStack(32);
   private final Vector4f scissorScratch = new Vector4f();
   private int fbo;
   private int width;
   private int height;
   private float scale;
   private MgfxRendererRepository rendererRepository;
   private MgfxBatchShaderProgram batch;

   public MgfxContext(int fbo, int width, int height, float scale) {
      this.update(fbo, width, height, scale);
   }

   public void update(int fbo, int width, int height, float scale) {
      this.fbo = fbo;
      this.width = width;
      this.height = height;
      this.scale = scale;
      this.reset();
   }

   public void initRepository() {
      this.rendererRepository = new MgfxRendererRepository(class_310.method_1551().method_1478(), this);
   }

   private MgfxRectangleRenderer rectangle() {
      return this.rendererRepository.getRectangleRenderer();
   }

   private MgfxSandboxRenderer sandbox() {
      this.ensureBatchFlush();
      return this.rendererRepository.getSandboxRenderer();
   }

   private MgfxFontRenderer font() {
      return this.rendererRepository.getFontRenderer();
   }

   public void drawRectangle(Rectangle rect) {
      this.executeBatch(this.rectangle());
      this.rectangle().render(rect);
   }

   public void drawFont(Msdf text) {
      this.executeBatch(this.font());
      this.font().render(text);
   }

   private void executeBatch(MgfxBatchShaderProgram batch) {
      if (this.batch != batch) {
         if (this.batch != null) {
            this.batch.end();
         }

         this.batch = batch;
         this.batch.begin();
      }
   }

   public void startScissor(float x, float y, float width, float height) {
      if (this.batch != null) {
         this.batch.flush();
      }

      GlStateManager._enableScissorTest();
      this.scissorScratch.set(x * this.scale, y * this.scale, 0.0F, 1.0F);
      this.matrixStack.transform(this.scissorScratch);
      float x1 = this.scissorScratch.x;
      float y1 = this.scissorScratch.y;
      this.scissorScratch.set((x + width) * this.scale, (y + height) * this.scale, 0.0F, 1.0F);
      this.matrixStack.transform(this.scissorScratch);
      float x2 = this.scissorScratch.x;
      float y2 = this.scissorScratch.y;
      float minX = Math.min(x1, x2);
      float minY = Math.min(y1, y2);
      float maxX = Math.max(x1, x2);
      float maxY = Math.max(y1, y2);
      int sx = (int)minX;
      int sy = (int)((float)this.height - maxY);
      int sw = (int)(maxX - minX);
      int sh = (int)(maxY - minY);
      if (sw < 0) {
         sw = 0;
      }

      if (sh < 0) {
         sh = 0;
      }

      GL30.glScissor(sx, sy, sw, sh);
   }

   public void endScissor() {
      if (this.batch != null) {
         this.batch.flush();
      }

      GlStateManager._disableScissorTest();
   }

   public void setFbo(int fbo) {
      if (this.fbo != fbo) {
         this.ensureBatchFlush();
         this.fbo = fbo;
      }

   }

   private void ensureBatchFlush() {
      if (this.batch != null) {
         this.batch.end();
         this.batch = null;
      }

   }

   public void push() {
      this.matrixStack.pushMatrix();
   }

   public void pop() {
      this.matrixStack.popMatrix();
   }

   public void translate(float x, float y, float z) {
      this.matrixStack.translate(x, y, z);
   }

   public void translate(float x, float y) {
      this.matrixStack.translate(x, y, 0.0F);
   }

   public void scale(float x, float y, float z) {
      this.matrixStack.scale(x, y, z);
   }

   public void scale(float scale) {
      this.matrixStack.scale(scale, scale, 1.0F);
   }

   public void rotate(float angleDeg, float x, float y, float z) {
      this.matrixStack.rotate((float)Math.toRadians((double)angleDeg), x, y, z);
   }

   public void rotateZ(float angleDeg) {
      this.matrixStack.rotate((float)Math.toRadians((double)angleDeg), 0.0F, 0.0F, 1.0F);
   }

   public void reset() {
      this.matrixStack.clear();
      this.matrixStack.identity();
      if (this.batch != null) {
         this.batch.flush();
      }

      GlStateManager._disableScissorTest();
   }

   public void finishFrame() {
      if (this.batch != null) {
         this.batch.end();
         this.batch = null;
      }

      Rectangle.frameReset();
      Msdf.frameReset();
      Texture.frameReset();
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      GlStateManager._depthMask(true);
      GlStateManager._enableBlend();
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      GlStateManager._colorMask(true, true, true, true);
      GlStateManager._disableCull();
   }

   @Generated
   public Matrix4fStack getMatrixStack() {
      return this.matrixStack;
   }

   @Generated
   public Vector4f getScissorScratch() {
      return this.scissorScratch;
   }

   @Generated
   public int getFbo() {
      return this.fbo;
   }

   @Generated
   public int getWidth() {
      return this.width;
   }

   @Generated
   public int getHeight() {
      return this.height;
   }

   @Generated
   public float getScale() {
      return this.scale;
   }

   @Generated
   public MgfxRendererRepository getRendererRepository() {
      return this.rendererRepository;
   }

   @Generated
   public MgfxBatchShaderProgram getBatch() {
      return this.batch;
   }

   @Generated
   public void setScale(float scale) {
      this.scale = scale;
   }
}
