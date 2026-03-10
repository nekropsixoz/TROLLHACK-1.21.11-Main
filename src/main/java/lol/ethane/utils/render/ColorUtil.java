package lol.ethane.utils.render;

import java.awt.Color;
import lol.ethane.utils.math.MathUtil;
import lombok.Generated;

public final class ColorUtil {
   public static int interpolate(int color1, int color2, float factor) {
      if (factor <= 0.0F) {
         return color1;
      } else if (factor >= 1.0F) {
         return color2;
      } else {
         int a1 = color1 >> 24 & 255;
         int r1 = color1 >> 16 & 255;
         int g1 = color1 >> 8 & 255;
         int b1 = color1 & 255;
         int a2 = color2 >> 24 & 255;
         int r2 = color2 >> 16 & 255;
         int g2 = color2 >> 8 & 255;
         int b2 = color2 & 255;
         int a = (int)((float)a1 + (float)(a2 - a1) * factor);
         int r = (int)((float)r1 + (float)(r2 - r1) * factor);
         int g = (int)((float)g1 + (float)(g2 - g1) * factor);
         int b = (int)((float)b1 + (float)(b2 - b1) * factor);
         return a << 24 | r << 16 | g << 8 | b;
      }
   }

   public static int interpolate(int color1, int color2, double factor) {
      return interpolate(color1, color2, (float)factor);
   }

   public static int applyOpacity(int color, float opacityFactor) {
      int alpha = (int)((float)(color >> 24 & 255) * opacityFactor);
      return color & 16777215 | alpha << 24;
   }

   public static int applyOpacity(int color, int opacity) {
      return color & 16777215 | (opacity & 255) << 24;
   }

   public static int getWaveColor(Color color1, Color color2, double offset) {
      float factor = (MathUtil.fastSin((double)(System.currentTimeMillis() % 1000000L) / 1000.0D * 3.0D + offset * 0.05D) + 1.0F) / 2.0F;
      return interpolate(color1.getRGB(), color2.getRGB(), factor);
   }

   public static int getWaveColor(int color1, int color2, double offset) {
      float factor = (MathUtil.fastSin((double)(System.currentTimeMillis() % 1000000L) / 1000.0D * 3.0D + offset * 0.05D) + 1.0F) / 2.0F;
      return interpolate(color1, color2, factor);
   }

   @Generated
   private ColorUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
