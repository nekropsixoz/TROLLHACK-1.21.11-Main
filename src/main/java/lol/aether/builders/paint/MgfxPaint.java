package lol.aether.builders.paint;

import java.awt.Color;
import lombok.Generated;

public class MgfxPaint {
   private static final MgfxPaint INSTANCE = new MgfxPaint();
   private MgfxPaintType type;
   private int color1;
   private int color2;
   private int color3;
   private int color4;
   private float x1;
   private float y1;
   private float x2;
   private float y2;

   public static MgfxPaint builder() {
      return INSTANCE.reset();
   }

   public MgfxPaint() {
      this.reset();
   }

   public MgfxPaint reset() {
      this.type = MgfxPaintType.STANDARD;
      this.color1 = this.color2 = this.color3 = this.color4 = -1;
      this.x1 = 0.0F;
      this.y1 = 0.0F;
      this.x2 = 0.0F;
      this.y2 = 0.0F;
      return this;
   }

   public MgfxPaint type(MgfxPaintType type) {
      this.type = type;
      return this;
   }

   public MgfxPaint color(int color) {
      this.type = MgfxPaintType.STANDARD;
      this.color1 = this.color2 = this.color3 = this.color4 = color;
      return this;
   }

   public MgfxPaint color(Color color) {
      return this.color(color.getRGB());
   }

   public MgfxPaint gradient(int c1, int c2, float x1, float y1, float x2, float y2) {
      this.type = MgfxPaintType.LINEAR_GRADIENT;
      this.color1 = c1;
      this.color2 = c2;
      this.color3 = c2;
      this.color4 = c1;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      return this;
   }

   public MgfxPaint verticalGradient(int topColor, int bottomColor) {
      this.type = MgfxPaintType.DIRECTIONAL_GRADIENT;
      this.color1 = topColor;
      this.color2 = topColor;
      this.color3 = bottomColor;
      this.color4 = bottomColor;
      return this;
   }

   public MgfxPaint horizontalGradient(int leftColor, int rightColor) {
      this.type = MgfxPaintType.DIRECTIONAL_GRADIENT;
      this.color1 = leftColor;
      this.color2 = rightColor;
      this.color3 = rightColor;
      this.color4 = leftColor;
      return this;
   }

   public MgfxPaint quadGradient(int tl, int tr, int br, int bl) {
      this.type = MgfxPaintType.QUAD_GRADIENT;
      this.color1 = tl;
      this.color2 = tr;
      this.color3 = br;
      this.color4 = bl;
      return this;
   }

   public void copyFrom(MgfxPaint other) {
      this.type = other.type;
      this.color1 = other.color1;
      this.color2 = other.color2;
      this.color3 = other.color3;
      this.color4 = other.color4;
      this.x1 = other.x1;
      this.y1 = other.y1;
      this.x2 = other.x2;
      this.y2 = other.y2;
   }

   public boolean matches(MgfxPaint other) {
      if (other == null) {
         return false;
      } else if (this == other) {
         return true;
      } else {
         return this.type == other.type && this.color1 == other.color1 && this.color2 == other.color2 && this.color3 == other.color3 && this.color4 == other.color4 && this.x1 == other.x1 && this.y1 == other.y1 && this.x2 == other.x2 && this.y2 == other.y2;
      }
   }

   @Generated
   public MgfxPaintType getType() {
      return this.type;
   }

   @Generated
   public int getColor1() {
      return this.color1;
   }

   @Generated
   public int getColor2() {
      return this.color2;
   }

   @Generated
   public int getColor3() {
      return this.color3;
   }

   @Generated
   public int getColor4() {
      return this.color4;
   }

   @Generated
   public float getX1() {
      return this.x1;
   }

   @Generated
   public float getY1() {
      return this.y1;
   }

   @Generated
   public float getX2() {
      return this.x2;
   }

   @Generated
   public float getY2() {
      return this.y2;
   }
}
