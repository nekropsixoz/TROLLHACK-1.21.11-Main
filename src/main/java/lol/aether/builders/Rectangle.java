package lol.aether.builders;

import java.awt.Color;
import lol.aether.builders.paint.MgfxPaint;
import lombok.Generated;

public class Rectangle {
   private static final FastPool<Rectangle> POOL = new FastPool(9000, Rectangle::new);
   private float x;
   private float y;
   private float width;
   private float height;
   private float tl;
   private float tr;
   private float br;
   private float bl;
   private int c1;
   private int c2;
   private int c3;
   private int c4;

   public static Rectangle builder() {
      return ((Rectangle)POOL.obtain()).reset();
   }

   public static void frameReset() {
      POOL.flush();
   }

   private Rectangle() {
      this.reset();
   }

   public Rectangle reset() {
      this.x = this.y = this.width = this.height = 0.0F;
      this.tl = this.tr = this.br = this.bl = 0.0F;
      this.c1 = this.c2 = this.c3 = this.c4 = -1;
      return this;
   }

   public Rectangle xywh(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      return this;
   }

   public Rectangle radius(float radius) {
      this.tl = this.tr = this.br = this.bl = radius;
      return this;
   }

   public Rectangle radius(float tl, float tr, float br, float bl) {
      this.tl = tl;
      this.tr = tr;
      this.br = br;
      this.bl = bl;
      return this;
   }

   public Rectangle paint(MgfxPaint paint) {
      this.c1 = paint.getColor1();
      this.c2 = paint.getColor2();
      this.c3 = paint.getColor3();
      this.c4 = paint.getColor4();
      return this;
   }

   public Rectangle color(int color) {
      this.c1 = this.c2 = this.c3 = this.c4 = color;
      return this;
   }

   public Rectangle color(Color color) {
      return this.color(color.getRGB());
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getTl() {
      return this.tl;
   }

   @Generated
   public float getTr() {
      return this.tr;
   }

   @Generated
   public float getBr() {
      return this.br;
   }

   @Generated
   public float getBl() {
      return this.bl;
   }

   @Generated
   public int getC1() {
      return this.c1;
   }

   @Generated
   public int getC2() {
      return this.c2;
   }

   @Generated
   public int getC3() {
      return this.c3;
   }

   @Generated
   public int getC4() {
      return this.c4;
   }
}
