package lol.ethane.feature.module.property.impl;

import java.awt.Color;
import lol.ethane.feature.module.property.Property;

public final class ColorProperty extends Property<Color> {
   private int rgb;

   public ColorProperty(String name, Color value) {
      super(name);
      this.setValue(value);
   }

   public int getRGB() {
      return this.rgb;
   }

   public void setValue(Color value) {
      super.setValue(value);
      this.rgb = value.getRGB();
   }

   public void applyValue(Object propertyValue) {
      if (propertyValue instanceof Number) {
         Number num = (Number)propertyValue;
         this.setValue(new Color(num.intValue(), true));
      } else if (propertyValue instanceof String) {
         String str = (String)propertyValue;

         try {
            String hex = str.replace("#", "");
            if (hex.length() == 6) {
               this.setValue(new Color(Integer.parseInt(hex, 16)));
            } else {
               this.setValue(new Color((int)Long.parseLong(hex, 16), true));
            }
         } catch (NumberFormatException var5) {
         }
      }

   }
}
