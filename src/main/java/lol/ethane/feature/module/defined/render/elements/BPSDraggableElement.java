package lol.ethane.feature.module.defined.render.elements;

import java.awt.Color;
import java.util.Locale;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.feature.drag.DraggableComponent;
import lol.ethane.feature.module.defined.render.InterfaceModule;
import lol.ethane.utils.render.ColorUtil;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_408;

public class BPSDraggableElement extends DraggableComponent {
   private float outlineAlpha;

   public BPSDraggableElement() {
      super("BPS", 10.0F, 30.0F, 50.0F, 15.0F, 4.0F);
   }

   protected void onRender(MgfxContext context, class_332 graphics, float x, float y, float w, float h) {
      InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
      boolean productSans = interfaceModule.fontTypeProperty.getValue() == InterfaceModule.FontType.PRODUCT_SANS;
      String label = "BPS";
      if ((Boolean)interfaceModule.lowerCaseProperty.getValue()) {
         label = label.toLowerCase();
      }

      double deltaX = class_310.method_1551().field_1724.method_23317() - class_310.method_1551().field_1724.field_6014;
      double deltaZ = class_310.method_1551().field_1724.method_23321() - class_310.method_1551().field_1724.field_5969;
      double bps = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 20.0D;
      Locale var10000 = Locale.US;
      Object[] var10002 = new Object[]{bps};
      String value = " " + String.format(var10000, "%.2f", var10002);
      float fontSize = 10.5F;
      float labelWidth;
      float valueWidth;
      if (productSans) {
         labelWidth = interfaceModule.font.getFont().width(label, fontSize);
         valueWidth = interfaceModule.font.getFont().width(value, fontSize);
      } else {
         labelWidth = (float)class_310.method_1551().field_1772.method_1727(label);
         valueWidth = (float)class_310.method_1551().field_1772.method_1727(value);
      }

      this.setWidth(labelWidth + valueWidth + 8.0F);
      this.setHeight(productSans ? 16.0F : 15.0F);
      context.drawRectangle(Rectangle.builder().xywh(x, y, this.getWidth(), this.getHeight()).radius(this.getRoundness()).color(new Color(15, 15, 15, 180)));
      float targetAlpha = class_310.method_1551().field_1755 instanceof class_408 ? 1.0F : 0.0F;
      this.outlineAlpha += (targetAlpha - this.outlineAlpha) * 0.15F;
      Color color1 = (Color)interfaceModule.primaryColorProperty.getValue();
      Color color2 = (Color)interfaceModule.secondaryColorProperty.getValue();
      float currentX = x + 4.0F;
      if (!productSans) {
         for(int i = 0; i < label.length(); ++i) {
            char c = label.charAt(i);
            String s = String.valueOf(c);
            int color = ColorUtil.getWaveColor(color1, color2, (double)(i * 20));
            graphics.method_25303(class_310.method_1551().field_1772, s, (int)currentX, (int)(y + 4.5F), color);
            currentX += (float)class_310.method_1551().field_1772.method_1727(s);
         }
      } else {
         context.drawFont(Msdf.builder().font(interfaceModule.font.getFont()).text(label).size(fontSize).xy(currentX, y + 1.5F).paint(MgfxPaint.builder().gradient(ColorUtil.getWaveColor(color1.getRGB(), color2.getRGB(), (double)currentX), ColorUtil.getWaveColor(color1.getRGB(), color2.getRGB(), (double)(currentX + interfaceModule.font.getFont().width(label, fontSize) + 15.0F)), currentX, y, currentX + interfaceModule.font.getFont().width(label, fontSize), y + 15.5F)));
         currentX += interfaceModule.font.getFont().width(label, fontSize);
      }

      if (productSans) {
         context.drawFont(Msdf.builder().font(interfaceModule.font.getFont()).text(value).size(fontSize).xy(currentX, y + 2.0F).color(-1));
      } else {
         graphics.method_25303(class_310.method_1551().field_1772, value, (int)currentX + 1, (int)(y + 4.5F), -1);
      }

   }

   protected void onRenderBloom(class_332 graphics, float x, float y, float w, float h) {
   }
}
