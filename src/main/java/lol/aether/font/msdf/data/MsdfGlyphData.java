package lol.aether.font.msdf.data;

import lombok.Generated;

public class MsdfGlyphData {
   private int unicode;
   private float advance;
   private MsdfBoundsData planeBounds;
   private MsdfBoundsData atlasBounds;

   @Generated
   public int getUnicode() {
      return this.unicode;
   }

   @Generated
   public float getAdvance() {
      return this.advance;
   }

   @Generated
   public MsdfBoundsData getPlaneBounds() {
      return this.planeBounds;
   }

   @Generated
   public MsdfBoundsData getAtlasBounds() {
      return this.atlasBounds;
   }
}
