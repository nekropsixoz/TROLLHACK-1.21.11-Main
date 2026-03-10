package lol.aether.builders;

import java.awt.Color;
import lol.aether.builders.paint.MgfxPaint;
import lombok.Generated;

public class Texture {
   private static final FastPool<Texture> POOL = new FastPool(10000, Texture::new);
   private float x;
   private float y;
   private float width;
   private float height;
   private float uMin;
   private float vMin;
   private float uMax;
   private float vMax;
   private float tl;
   private float tr;
   private float br;
   private float bl;
   private int textureId;
   private int c1;
   private int c2;
   private int c3;
   private int c4;

   public static Texture builder() {
      return ((Texture)POOL.obtain()).reset();
   }

   public static void frameReset() {
      POOL.flush();
   }

   private Texture() {
      this.reset();
   }

   public Texture reset() {
      this.x = this.y = this.width = this.height = 0.0F;
      this.uMin = this.vMin = 0.0F;
      this.uMax = this.vMax = 1.0F;
      this.tl = this.tr = this.br = this.bl = 0.0F;
      this.textureId = 0;
      this.c1 = this.c2 = this.c3 = this.c4 = -1;
      return this;
   }

   public Texture xywh(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      return this;
   }

   public Texture textureId(int textureId) {
      this.textureId = textureId;
      return this;
   }

   public Texture uv(float uMin, float vMin, float uMax, float vMax) {
      this.uMin = uMin;
      this.vMin = vMin;
      this.uMax = uMax;
      this.vMax = vMax;
      return this;
   }

   public Texture radius(float radius) {
      this.tl = this.tr = this.br = this.bl = radius;
      return this;
   }

   public Texture radius(float tl, float tr, float br, float bl) {
      this.tl = tl;
      this.tr = tr;
      this.br = br;
      this.bl = bl;
      return this;
   }

   public Texture paint(MgfxPaint paint) {
      this.c1 = paint.getColor1();
      this.c2 = paint.getColor2();
      this.c3 = paint.getColor3();
      this.c4 = paint.getColor4();
      return this;
   }

   public Texture color(int color) {
      this.c1 = this.c2 = this.c3 = this.c4 = color;
      return this;
   }

   public Texture color(Color color) {
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
   public float getUMin() {
      return this.uMin;
   }

   @Generated
   public float getVMin() {
      return this.vMin;
   }

   @Generated
   public float getUMax() {
      return this.uMax;
   }

   @Generated
   public float getVMax() {
      return this.vMax;
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
   public int getTextureId() {
      return this.textureId;
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
