package lol.aether.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.class_3300;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public abstract class MgfxBatchShaderProgram extends MgfxShaderProgram {
   protected final int vao;
   protected final int vbo;
   protected final int ebo;
   protected final FloatBuffer vertexBuffer;
   protected int rectCount;
   protected final int vertexSize;
   private final Matrix4f projectionMatrix = new Matrix4f();
   private final float[] matrixBuffer = new float[16];
   private int cachedWidth = -1;
   private int cachedHeight = -1;
   private final Vector4f batchScratch = new Vector4f();

   public MgfxBatchShaderProgram(class_3300 resourceManager, MgfxContext context, String vertPath, String fragPath, int vertexSize) {
      super(resourceManager, context, vertPath, fragPath);
      this.vertexSize = vertexSize;
      this.vao = GL33.glGenVertexArrays();
      this.vbo = GL33.glGenBuffers();
      this.ebo = GL33.glGenBuffers();
      this.vertexBuffer = MemoryUtil.memAllocFloat('負' * vertexSize);
      GL33.glBindVertexArray(this.vao);
      GL15.glBindBuffer(34962, this.vbo);
      GL33.glBufferData(34962, (long)this.vertexBuffer.capacity() * 4L, 35048);
      int[] indices = new int['티'];
      int offset = 0;

      for(int i = 0; i < 9000; ++i) {
         indices[i * 6] = offset;
         indices[i * 6 + 1] = offset + 1;
         indices[i * 6 + 2] = offset + 2;
         indices[i * 6 + 3] = offset + 2;
         indices[i * 6 + 4] = offset + 3;
         indices[i * 6 + 5] = offset;
         offset += 4;
      }

      IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
      indexBuffer.put(indices).flip();
      GL15.glBindBuffer(34963, this.ebo);
      GL33.glBufferData(34963, indexBuffer, 35044);
      MemoryUtil.memFree(indexBuffer);
      this.setupAttributes(vertexSize * 4);
      GL33.glBindVertexArray(0);
   }

   protected abstract void setupAttributes(int var1);

   protected void putTransformedVertex(float x, float y) {
      this.batchScratch.set(x, y, 0.0F, 1.0F);
      this.getContext().getMatrixStack().transform(this.batchScratch);
      this.vertexBuffer.put(this.batchScratch.x).put(this.batchScratch.y);
   }

   protected void init() {
      this.register("u_Projection");
   }

   public void cleanup() {
      super.cleanup();
      GL33.glDeleteBuffers(this.vbo);
      GL33.glDeleteBuffers(this.ebo);
      GL33.glDeleteVertexArrays(this.vao);
      MemoryUtil.memFree(this.vertexBuffer);
   }

   public void flush() {
      if (this.rectCount != 0) {
         this.before();
         this.vertexBuffer.flip();
         GL33.glBindVertexArray(this.vao);
         GL15.glBindBuffer(34962, this.vbo);
         GL33.glBufferSubData(34962, 0L, this.vertexBuffer);
         GL33.glDrawElements(4, this.rectCount * 6, 5125, 0L);
         this.rectCount = 0;
         this.vertexBuffer.clear();
         this.after();
      }
   }

   public void end() {
      this.flush();
      this.unbind();
      GL33.glBindVertexArray(0);
   }

   protected void before() {
   }

   protected void after() {
   }

   public void begin() {
      this.bind();
      this.rectCount = 0;
      this.vertexBuffer.clear();
      GlStateManager._glBindFramebuffer(36160, this.getContext().getFbo());
      GL11.glViewport(0, 0, this.getContext().getWidth(), this.getContext().getHeight());
      if (this.getContext().getWidth() != this.cachedWidth || this.getContext().getHeight() != this.cachedHeight) {
         this.projectionMatrix.setOrtho(0.0F, (float)this.getContext().getWidth(), (float)this.getContext().getHeight(), 0.0F, -1.0F, 1.0F);
         this.projectionMatrix.get(this.matrixBuffer);
         GL33.glUniformMatrix4fv((Integer)this.getUniformCache().getOrDefault("u_Projection", -1), false, this.matrixBuffer);
         this.cachedWidth = this.getContext().getWidth();
         this.cachedHeight = this.getContext().getHeight();
      }

      this.enableBlend();
      this.setBlendFuncSeparate(770, 771, 770, 771);
      this.disableDepth();
   }
}
