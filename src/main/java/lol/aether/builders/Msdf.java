package lol.aether.builders;

import java.awt.Color;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.msdf.MsdfFont;
import lombok.Generated;

public class Msdf {
   private static final FastPool<Msdf> POOL = new FastPool(10000, Msdf::new);
   private String text;
   private float x;
   private float y;
   private float size;
   private MsdfFont font;
   private boolean align;
   private float alignWidth;
   private float alignHeight;
   private boolean shadow;
   private final MgfxPaint paint = new MgfxPaint();

   public static Msdf builder() {
      return ((Msdf)POOL.obtain()).reset();
   }

   public static void frameReset() {
      POOL.flush();
   }

   private Msdf() {
      this.reset();
   }

   public Msdf reset() {
      this.text = "";
      this.x = this.y = 0.0F;
      this.size = 10.0F;
      this.font = null;
      this.paint.reset();
      this.align = false;
      this.alignWidth = 0.0F;
      this.alignHeight = 0.0F;
      this.shadow = false;
      return this;
   }

   public Msdf text(String text) {
      this.text = text;
      return this;
   }

   public Msdf align(float alignWidth, float alignHeight) {
      this.align = true;
      this.alignWidth = alignWidth;
      this.alignHeight = alignHeight;
      return this;
   }

   public Msdf xy(float x, float y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Msdf size(float size) {
      this.size = size;
      return this;
   }

   public Msdf font(MsdfFont font) {
      this.font = font;
      return this;
   }

   public Msdf shadow() {
      this.shadow = true;
      return this;
   }

   public Msdf paint(MgfxPaint paint) {
      this.paint.copyFrom(paint);
      return this;
   }

   public Msdf color(int color) {
      this.paint.color(color);
      return this;
   }

   public Msdf color(Color color) {
      this.paint.color(color);
      return this;
   }

   @Generated
   public String getText() {
      return this.text;
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
   public float getSize() {
      return this.size;
   }

   @Generated
   public MsdfFont getFont() {
      return this.font;
   }

   @Generated
   public boolean isAlign() {
      return this.align;
   }

   @Generated
   public float getAlignWidth() {
      return this.alignWidth;
   }

   @Generated
   public float getAlignHeight() {
      return this.alignHeight;
   }

   @Generated
   public boolean isShadow() {
      return this.shadow;
   }

   @Generated
   public MgfxPaint getPaint() {
      return this.paint;
   }
}
