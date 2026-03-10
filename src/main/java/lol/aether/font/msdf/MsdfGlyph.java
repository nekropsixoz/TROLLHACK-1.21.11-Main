package lol.aether.font.msdf;

import lol.aether.font.msdf.data.MsdfBoundsData;
import lol.aether.font.msdf.data.MsdfGlyphData;
import lombok.Generated;

public final class MsdfGlyph {
   private final float advance;
   private final float planeLeft;
   private final float planeTop;
   private final float planeWidth;
   private final float planeHeight;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private final boolean hasGeometry;

   public MsdfGlyph(MsdfGlyphData data, float atlasWidth, float atlasHeight) {
      this.advance = data.getAdvance();
      MsdfBoundsData plane = data.getPlaneBounds();
      if (plane != null) {
         this.planeLeft = plane.getLeft();
         this.planeTop = plane.getTop();
         this.planeWidth = plane.getRight() - plane.getLeft();
         this.planeHeight = plane.getTop() - plane.getBottom();
         this.hasGeometry = true;
      } else {
         this.planeLeft = this.planeTop = this.planeWidth = this.planeHeight = 0.0F;
         this.hasGeometry = false;
      }

      MsdfBoundsData atlas = data.getAtlasBounds();
      if (atlas != null) {
         this.minU = atlas.getLeft() / atlasWidth;
         this.maxU = atlas.getRight() / atlasWidth;
         this.minV = 1.0F - atlas.getTop() / atlasHeight;
         this.maxV = 1.0F - atlas.getBottom() / atlasHeight;
      } else {
         this.minU = this.maxU = this.minV = this.maxV = 0.0F;
      }

   }

   @Generated
   public float getAdvance() {
      return this.advance;
   }

   @Generated
   public float getPlaneLeft() {
      return this.planeLeft;
   }

   @Generated
   public float getPlaneTop() {
      return this.planeTop;
   }

   @Generated
   public float getPlaneWidth() {
      return this.planeWidth;
   }

   @Generated
   public float getPlaneHeight() {
      return this.planeHeight;
   }

   @Generated
   public float getMinU() {
      return this.minU;
   }

   @Generated
   public float getMaxU() {
      return this.maxU;
   }

   @Generated
   public float getMinV() {
      return this.minV;
   }

   @Generated
   public float getMaxV() {
      return this.maxV;
   }

   @Generated
   public boolean isHasGeometry() {
      return this.hasGeometry;
   }
}
