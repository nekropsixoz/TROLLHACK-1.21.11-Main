package lol.aether.font.msdf.data;

import com.google.gson.annotations.SerializedName;
import lombok.Generated;

public class MsdfKerningData {
   @SerializedName("unicode1")
   private int leftChar;
   @SerializedName("unicode2")
   private int rightChar;
   private float advance;

   @Generated
   public int getLeftChar() {
      return this.leftChar;
   }

   @Generated
   public int getRightChar() {
      return this.rightChar;
   }

   @Generated
   public float getAdvance() {
      return this.advance;
   }
}
