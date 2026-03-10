package lol.aether.renderer.batch;

import java.util.Map;
import lol.aether.builders.Msdf;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.builders.paint.MgfxPaintType;
import lol.aether.font.msdf.MsdfFont;
import lol.aether.font.msdf.MsdfGlyph;
import lol.aether.shader.MgfxBatchShaderProgram;
import lol.aether.shader.MgfxContext;
import net.minecraft.class_3300;
import org.lwjgl.opengl.GL33;

public class MgfxFontRenderer extends MgfxBatchShaderProgram {
   private int currentTextureId = -1;
   private float currentRange = 8.0F;
   private float currentAtlasWidth = 512.0F;
   private float currentAtlasHeight = 512.0F;
   private final MgfxPaint currentPaint = new MgfxPaint();
   private static final MgfxPaint WHITE_PAINT = (new MgfxPaint()).color(-1);

   public MgfxFontRenderer(class_3300 resourceManager, MgfxContext context) {
      super(resourceManager, context, "shaders/text/shader.vert", "shaders/text/shader.frag", 8);
   }

   protected void setupAttributes(int stride) {
      GL33.glVertexAttribPointer(0, 2, 5126, false, stride, 0L);
      GL33.glEnableVertexAttribArray(0);
      GL33.glVertexAttribPointer(1, 2, 5126, false, stride, 8L);
      GL33.glEnableVertexAttribArray(1);
      GL33.glVertexAttribPointer(2, 4, 5126, false, stride, 16L);
      GL33.glEnableVertexAttribArray(2);
   }

   protected void init() {
      super.init();
      this.register("u_Texture");
      this.register("u_Range");
      this.register("u_TextureSize");
      this.register("u_PaintType");
      this.register("u_PaintColor1");
      this.register("u_PaintColor2");
      this.register("u_GradientCoords");
   }

   protected void before() {
      this.enableBlend();
      this.setBlendFuncSeparate(770, 771, 770, 771);
      this.disableCull();
      this.disableDepth();
      this.setActiveTexture(33984);
      this.bindTexture(this.currentTextureId);
      int loc = (Integer)this.getUniformCache().getOrDefault("u_Texture", -1);
      if (loc != -1) {
         GL33.glUniform1i(loc, 0);
      }

   }

   public void flush() {
      if (this.rectCount != 0) {
         GL33.glUniform1f((Integer)this.getUniformCache().getOrDefault("u_Range", -1), this.currentRange);
         GL33.glUniform2f((Integer)this.getUniformCache().getOrDefault("u_TextureSize", -1), this.currentAtlasWidth, this.currentAtlasHeight);
         GL33.glUniform1i((Integer)this.getUniformCache().getOrDefault("u_PaintType", -1), this.currentPaint.getType().getIndex());
         float pA1 = (float)(this.currentPaint.getColor1() >> 24 & 255) / 255.0F;
         float pR1 = (float)(this.currentPaint.getColor1() >> 16 & 255) / 255.0F;
         float pG1 = (float)(this.currentPaint.getColor1() >> 8 & 255) / 255.0F;
         float pB1 = (float)(this.currentPaint.getColor1() & 255) / 255.0F;
         GL33.glUniform4f((Integer)this.getUniformCache().getOrDefault("u_PaintColor1", -1), pR1, pG1, pB1, pA1);
         if (this.currentPaint.getType() == MgfxPaintType.LINEAR_GRADIENT) {
            float pA2 = (float)(this.currentPaint.getColor2() >> 24 & 255) / 255.0F;
            float pR2 = (float)(this.currentPaint.getColor2() >> 16 & 255) / 255.0F;
            float pG2 = (float)(this.currentPaint.getColor2() >> 8 & 255) / 255.0F;
            float pB2 = (float)(this.currentPaint.getColor2() & 255) / 255.0F;
            GL33.glUniform4f((Integer)this.getUniformCache().getOrDefault("u_PaintColor2", -1), pR2, pG2, pB2, pA2);
            float scale = this.getContext().getScale();
            GL33.glUniform4f((Integer)this.getUniformCache().getOrDefault("u_GradientCoords", -1), this.currentPaint.getX1() * scale, this.currentPaint.getY1() * scale, this.currentPaint.getX2() * scale, this.currentPaint.getY2() * scale);
         }

         super.flush();
      }
   }

   public void render(Msdf text) {
      float scale = this.getContext().getScale();
      float scaledSize = text.getSize() * scale;
      float alignWidth = text.getAlignWidth() * scale;
      float alignHeight = text.getAlignHeight() * scale;
      float screenX = text.getX() * scale;
      float screenY = text.getY() * scale;
      float finalX = text.isAlign() ? screenX + (alignWidth - text.getFont().width(text.getText(), scaledSize)) / 2.0F : screenX;
      float finalY = text.isAlign() ? screenY + (alignHeight - text.getFont().lineHeight(scaledSize)) / 2.0F : screenY;
      MsdfFont font = text.getFont();
      MgfxPaint textPaint = text.getPaint();
      boolean standardPaint = textPaint.getType() == MgfxPaintType.STANDARD;
      boolean paintChanged;
      if (standardPaint) {
         paintChanged = !this.currentPaint.matches(WHITE_PAINT);
      } else {
         paintChanged = !this.currentPaint.matches(textPaint);
      }

      if (font.getTextureId() != this.currentTextureId || paintChanged) {
         this.flush();
         this.currentTextureId = font.getTextureId();
         this.currentRange = font.getRange() > 0.0F ? font.getRange() : 4.0F;
         if (standardPaint) {
            this.currentPaint.copyFrom(WHITE_PAINT);
         } else {
            this.currentPaint.copyFrom(textPaint);
         }

         this.bindTexture(this.currentTextureId);
         this.currentAtlasWidth = font.getAtlasWidth();
         this.currentAtlasHeight = font.getAtlasHeight();
      }

      float currentR = 1.0F;
      float currentG = 1.0F;
      float currentB = 1.0F;
      float currentA = 1.0F;
      if (standardPaint) {
         int c = textPaint.getColor1();
         currentA = (float)(c >> 24 & 255) / 255.0F;
         currentR = (float)(c >> 16 & 255) / 255.0F;
         currentG = (float)(c >> 8 & 255) / 255.0F;
         currentB = (float)(c & 255) / 255.0F;
      }

      float currentX = finalX;
      float currentY = finalY + font.getAscender() * scaledSize;
      Map<Integer, MsdfGlyph> glyphs = font.getGlyphMap();
      Map<Integer, Map<Integer, Float>> kernings = font.getKerningMap();
      String str = text.getText();
      int len = str.length();
      int prevChar = -1;

      for(int i = 0; i < len; ++i) {
         char charCode = str.charAt(i);
         MsdfGlyph glyph = (MsdfGlyph)glyphs.get(Integer.valueOf(charCode));
         if (glyph != null) {
            if (prevChar != -1) {
               Map<Integer, Float> kerningMap = (Map)kernings.get(prevChar);
               if (kerningMap != null) {
                  Float kerning = (Float)kerningMap.get(Integer.valueOf(charCode));
                  if (kerning != null) {
                     currentX += kerning * scaledSize;
                  }
               }
            }

            if (glyph.isHasGeometry()) {
               if (this.rectCount >= 8999) {
                  this.flush();
               }

               float x0 = currentX + glyph.getPlaneLeft() * scaledSize;
               float y0 = currentY - glyph.getPlaneTop() * scaledSize;
               float w = glyph.getPlaneWidth() * scaledSize;
               float h = glyph.getPlaneHeight() * scaledSize;
               float uMin = glyph.getMinU();
               float vMin = glyph.getMinV();
               float uMax = glyph.getMaxU();
               float vMax = glyph.getMaxV();
               if (text.isShadow()) {
                  float shadowMult = 0.25F;
                  this.drawGlyphVertex(x0 + 1.0F, y0 + 1.0F, w, h, uMin, vMin, uMax, vMax, currentR * 0.25F, currentG * 0.25F, currentB * 0.25F, currentA);
               }

               this.drawGlyphVertex(x0, y0, w, h, uMin, vMin, uMax, vMax, currentR, currentG, currentB, currentA);
            }

            currentX += glyph.getAdvance() * scaledSize;
            prevChar = charCode;
         }
      }

   }

   private void drawGlyphVertex(float x0, float y0, float w, float h, float uMin, float vMin, float uMax, float vMax, float r, float g, float b, float a) {
      this.putTransformedVertex(x0, y0);
      this.vertexBuffer.put(uMin).put(vMin).put(r).put(g).put(b).put(a);
      this.putTransformedVertex(x0, y0 + h);
      this.vertexBuffer.put(uMin).put(vMax).put(r).put(g).put(b).put(a);
      this.putTransformedVertex(x0 + w, y0 + h);
      this.vertexBuffer.put(uMax).put(vMax).put(r).put(g).put(b).put(a);
      this.putTransformedVertex(x0 + w, y0);
      this.vertexBuffer.put(uMax).put(vMin).put(r).put(g).put(b).put(a);
      ++this.rectCount;
   }
}
