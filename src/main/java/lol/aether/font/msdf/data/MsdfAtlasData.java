package lol.aether.font.msdf.data;

import com.google.gson.annotations.SerializedName;
import lombok.Generated;

public class MsdfAtlasData {
   @SerializedName("distanceRange")
   private float range;
   private float width;
   private float height;

   @Generated
   public float getRange() {
      return this.range;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }
}
