package lol.aether.builders.paint;

import lombok.Generated;

public enum MgfxPaintType {
   STANDARD(0),
   LINEAR_GRADIENT(1),
   QUAD_GRADIENT(2),
   DIRECTIONAL_GRADIENT(3);

   final int index;

   private MgfxPaintType(final int index) {
      this.index = index;
   }

   @Generated
   public int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static MgfxPaintType[] $values() {
      return new MgfxPaintType[]{STANDARD, LINEAR_GRADIENT, QUAD_GRADIENT, DIRECTIONAL_GRADIENT};
   }
}
