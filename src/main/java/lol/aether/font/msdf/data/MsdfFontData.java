package lol.aether.font.msdf.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Generated;

public class MsdfFontData {
   private MsdfAtlasData atlas;
   private MsdfMetricsData metrics;
   @SerializedName("glyphs")
   private List<MsdfGlyphData> glyphData;
   @SerializedName("kerning")
   private List<MsdfKerningData> kerningData;

   @Generated
   public MsdfAtlasData getAtlas() {
      return this.atlas;
   }

   @Generated
   public MsdfMetricsData getMetrics() {
      return this.metrics;
   }

   @Generated
   public List<MsdfGlyphData> getGlyphData() {
      return this.glyphData;
   }

   @Generated
   public List<MsdfKerningData> getKerningData() {
      return this.kerningData;
   }
}
