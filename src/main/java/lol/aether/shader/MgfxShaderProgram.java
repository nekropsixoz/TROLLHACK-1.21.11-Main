package lol.aether.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.HashMap;
import java.util.Map;
import lol.aether.helper.MgfxShaderHelper;
import lombok.Generated;
import net.minecraft.class_3300;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public abstract class MgfxShaderProgram {
   protected final Vector4f scratchVec = new Vector4f();
   protected final int programId;
   private final int vaoId;
   private final Map<String, Integer> uniformCache = new HashMap();
   private final MgfxContext context;

   public MgfxShaderProgram(class_3300 resourceManager, MgfxContext context, String vertPath, String fragPath) {
      this.programId = MgfxShaderHelper.createShader(resourceManager, fragPath, vertPath);
      this.vaoId = GL33.glGenVertexArrays();
      this.init();
      this.context = context;
   }

   protected abstract void init();

   protected void register(String name) {
      int location = GL33.glGetUniformLocation(this.programId, name);
      if (location == -1) {
         System.err.println("Uniform not found: " + name);
      }

      this.uniformCache.put(name, location);
   }

   public void cleanup() {
      GL33.glDeleteVertexArrays(this.vaoId);
      GL33.glDeleteProgram(this.programId);
   }

   public void setFloat(String name, float value) {
      GL33.glUniform1f((Integer)this.uniformCache.getOrDefault(name, -1), value);
   }

   public void setVec2(String name, float x, float y) {
      GL33.glUniform2f((Integer)this.uniformCache.getOrDefault(name, -1), x, y);
   }

   public void bind() {
      GL20.glUseProgram(this.programId);
      GL30.glBindVertexArray(this.vaoId);
   }

   public void unbind() {
      GL30.glBindVertexArray(0);
      GL20.glUseProgram(0);
   }

   protected void enableScissor() {
      GlStateManager._enableScissorTest();
   }

   protected void disableScissor() {
      GlStateManager._disableScissorTest();
   }

   protected void enableBlend() {
      GlStateManager._enableBlend();
   }

   protected void setBlendFuncSeparate(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
      GlStateManager._blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
   }

   protected void renderFullScreen() {
      GL33.glDrawArrays(4, 0, 3);
   }

   protected void disableDepth() {
      GlStateManager._disableDepthTest();
   }

   protected void transformSize(float w, float h) {
      this.scratchVec.set(w, h, 0.0F, 0.0F);
      this.getContext().getMatrixStack().transform(this.scratchVec);
   }

   protected void disableCull() {
      GlStateManager._disableCull();
   }

   protected void setActiveTexture(int i) {
      GlStateManager._activeTexture(i);
   }

   protected void bindTexture(int i) {
      GlStateManager._bindTexture(i);
   }

   protected void setInt(String name, int val) {
      GL33.glUniform1i((Integer)this.getUniformCache().getOrDefault(name, -1), val);
   }

   protected void transformPos(float x, float y) {
      this.scratchVec.set(x, y, 0.0F, 1.0F);
      this.getContext().getMatrixStack().transform(this.scratchVec);
   }

   @Generated
   public Map<String, Integer> getUniformCache() {
      return this.uniformCache;
   }

   @Generated
   public MgfxContext getContext() {
      return this.context;
   }
}
