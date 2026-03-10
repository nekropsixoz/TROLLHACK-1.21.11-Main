package lol.aether.renderer.batch;

import lol.aether.builders.Rectangle;
import lol.aether.shader.MgfxBatchShaderProgram;
import lol.aether.shader.MgfxContext;
import net.minecraft.class_3300;
import org.lwjgl.opengl.GL33;

public class MgfxRectangleRenderer extends MgfxBatchShaderProgram {
   public MgfxRectangleRenderer(class_3300 resourceManager, MgfxContext context) {
      super(resourceManager, context, "shaders/rectangle/shader.vert", "shaders/rectangle/shader.frag", 14);
   }

   protected void setupAttributes(int stride) {
      GL33.glVertexAttribPointer(0, 2, 5126, false, stride, 0L);
      GL33.glEnableVertexAttribArray(0);
      GL33.glVertexAttribPointer(1, 4, 5126, false, stride, 8L);
      GL33.glEnableVertexAttribArray(1);
      GL33.glVertexAttribPointer(2, 2, 5126, false, stride, 24L);
      GL33.glEnableVertexAttribArray(2);
      GL33.glVertexAttribPointer(3, 4, 5126, false, stride, 32L);
      GL33.glEnableVertexAttribArray(3);
      GL33.glVertexAttribPointer(4, 2, 5126, false, stride, 48L);
      GL33.glEnableVertexAttribArray(4);
   }

   public void render(Rectangle rectangle) {
      if (this.rectCount >= 9000) {
         this.flush();
      }

      float scale = this.getContext().getScale();
      float px = rectangle.getX() * scale;
      float py = rectangle.getY() * scale;
      float pw = rectangle.getWidth() * scale;
      float ph = rectangle.getHeight() * scale;
      float pTl = rectangle.getTl() * scale;
      float pTr = rectangle.getTr() * scale;
      float pBr = rectangle.getBr() * scale;
      float pBl = rectangle.getBl() * scale;
      float halfW = pw / 2.0F;
      float halfH = ph / 2.0F;
      this.putVertex(px, py, rectangle.getC1(), pw, ph, pTl, pTr, pBr, pBl, -halfW, -halfH);
      this.putVertex(px, py + ph, rectangle.getC4(), pw, ph, pTl, pTr, pBr, pBl, -halfW, halfH);
      this.putVertex(px + pw, py + ph, rectangle.getC3(), pw, ph, pTl, pTr, pBr, pBl, halfW, halfH);
      this.putVertex(px + pw, py, rectangle.getC2(), pw, ph, pTl, pTr, pBr, pBl, halfW, -halfH);
      ++this.rectCount;
   }

   private void putVertex(float x, float y, int color, float w, float h, float r1, float r2, float r3, float r4, float lx, float ly) {
      this.putTransformedVertex(x, y);
      float cA = (float)(color >> 24 & 255) / 255.0F;
      float cR = (float)(color >> 16 & 255) / 255.0F;
      float cG = (float)(color >> 8 & 255) / 255.0F;
      float cB = (float)(color & 255) / 255.0F;
      this.vertexBuffer.put(cR).put(cG).put(cB).put(cA);
      this.vertexBuffer.put(w).put(h);
      this.vertexBuffer.put(r1).put(r2).put(r3).put(r4);
      this.vertexBuffer.put(lx).put(ly);
   }
}
