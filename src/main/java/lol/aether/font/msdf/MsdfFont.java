package lol.aether.font.msdf;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lol.aether.font.msdf.data.MsdfFontData;
import lol.aether.font.msdf.data.MsdfGlyphData;
import lol.aether.helper.MgfxTextureHelper;
import lol.ethane.mixin.accessor.AbstractTextureAccessor;
import lombok.Generated;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;

public class MsdfFont {
   private final String name;
   private final int textureId;
   private final float range;
   private final float ascender;
   private final float descender;
   private final float lineHeight;
   private final Map<Integer, MsdfGlyph> glyphMap;
   private final Map<Integer, Map<Integer, Float>> kerningMap;
   private float atlasWidth;
   private float atlasHeight;

   public static MsdfFont create(class_3300 manager, String name, String dataName, String atlasName) {
      String dataPath = "fonts/" + dataName + ".json";
      String atlasPath = "fonts/" + atlasName + ".png";
      MsdfFontData data = loadData(manager, dataPath);
      if (data == null) {
         throw new RuntimeException("Failed to read font data: " + dataPath);
      } else {
         class_1044 abstractTexture = class_310.method_1551().method_1531().method_4619(class_2960.method_60655("ethane", atlasPath));
         ((AbstractTextureAccessor)abstractTexture).setSampler(RenderSystem.getSamplerCache().method_75293(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, false));
         int textureId = MgfxTextureHelper.getTextureId(abstractTexture.method_68004());
         float aWidth = data.getAtlas().getWidth();
         float aHeight = data.getAtlas().getHeight();
         Map<Integer, MsdfGlyph> glyphs = (Map)data.getGlyphData().stream().collect(Collectors.toMap(MsdfGlyphData::getUnicode, (glyph) -> {
            return new MsdfGlyph(glyph, aWidth, aHeight);
         }));
         Map<Integer, Map<Integer, Float>> kerningData = new HashMap();
         if (data.getKerningData() != null) {
            data.getKerningData().forEach((kerning) -> {
               ((Map)kerningData.computeIfAbsent(kerning.getLeftChar(), (k) -> {
                  return new HashMap();
               })).put(kerning.getRightChar(), kerning.getAdvance());
            });
         }

         return new MsdfFont(name, textureId, data.getAtlas().getRange(), data.getMetrics().getAscender(), data.getMetrics().getDescender(), data.getMetrics().getLineHeight(), glyphs, kerningData, aWidth, aHeight);
      }
   }

   public float lineHeight(float size) {
      return this.lineHeight * size;
   }

   public float width(String text, float size) {
      int prevChar = -1;
      float width = 0.0F;
      int len = text.length();

      for(int i = 0; i < text.length(); ++i) {
         int _char = text.charAt(i);
         if (_char == 167 && i + 1 < len) {
            ++i;
            prevChar = -1;
         } else {
            MsdfGlyph glyph = (MsdfGlyph)this.glyphMap.get(Integer.valueOf(_char));
            if (glyph != null) {
               Map<Integer, Float> kerning = (Map)this.kerningMap.get(prevChar);
               if (kerning != null) {
                  width += (Float)kerning.getOrDefault(Integer.valueOf(_char), 0.0F) * size;
               }

               width += glyph.getAdvance() * size;
               prevChar = _char;
            }
         }
      }

      return width;
   }

   private static MsdfFontData loadData(class_3300 manager, String path) {
      try {
         InputStream stream = getStream(manager, path);

         MsdfFontData var4;
         try {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            try {
               var4 = (MsdfFontData)(new Gson()).fromJson(reader, MsdfFontData.class);
            } catch (Throwable var8) {
               try {
                  reader.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            reader.close();
         } catch (Throwable var9) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }
            }

            throw var9;
         }

         if (stream != null) {
            stream.close();
         }

         return var4;
      } catch (Exception var10) {
         var10.printStackTrace();
         return null;
      }
   }

   private static InputStream getStream(class_3300 manager, String path) throws IOException {
      class_2960 location = path.contains(":") ? class_2960.method_60654(path) : class_2960.method_60655("ethane", path);
      Optional<class_3298> resource = manager.method_14486(location);
      if (resource.isEmpty()) {
         throw new IOException("Resource not found: " + String.valueOf(location));
      } else {
         return ((class_3298)resource.get()).method_14482();
      }
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public int getTextureId() {
      return this.textureId;
   }

   @Generated
   public float getRange() {
      return this.range;
   }

   @Generated
   public float getAscender() {
      return this.ascender;
   }

   @Generated
   public float getDescender() {
      return this.descender;
   }

   @Generated
   public float getLineHeight() {
      return this.lineHeight;
   }

   @Generated
   public Map<Integer, MsdfGlyph> getGlyphMap() {
      return this.glyphMap;
   }

   @Generated
   public Map<Integer, Map<Integer, Float>> getKerningMap() {
      return this.kerningMap;
   }

   @Generated
   public float getAtlasWidth() {
      return this.atlasWidth;
   }

   @Generated
   public float getAtlasHeight() {
      return this.atlasHeight;
   }

   @Generated
   private MsdfFont(String name, int textureId, float range, float ascender, float descender, float lineHeight, Map<Integer, MsdfGlyph> glyphMap, Map<Integer, Map<Integer, Float>> kerningMap, float atlasWidth, float atlasHeight) {
      this.name = name;
      this.textureId = textureId;
      this.range = range;
      this.ascender = ascender;
      this.descender = descender;
      this.lineHeight = lineHeight;
      this.glyphMap = glyphMap;
      this.kerningMap = kerningMap;
      this.atlasWidth = atlasWidth;
      this.atlasHeight = atlasHeight;
   }
}
