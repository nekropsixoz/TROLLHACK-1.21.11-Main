package lol.ethane.feature.module.defined.render;

import java.awt.Color;
import java.util.Iterator;
import lol.aether.builders.Msdf;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.ethane.Ethane;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.defined.render.RenderBloomEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.drag.DraggableComponent;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.render.elements.BPSDraggableElement;
import lol.ethane.feature.module.defined.render.elements.FPSDraggableElement;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.utils.render.ColorUtil;
import lombok.Generated;

public class InterfaceModule extends Module {
   public final Property<InterfaceModule.FontType> fontTypeProperty;
   public final Property<Boolean> lowerCaseProperty;
   public final Property<Boolean> fpsProperty;
   public final Property<Boolean> bpsProperty;
   public final Property<Color> primaryColorProperty;
   public final Property<Color> secondaryColorProperty;
   public final MgfxFont font;

   public InterfaceModule() {
      super("Interface", "Displays the client.", ModuleCategory.RENDER);
      this.fontTypeProperty = new EnumProperty("Font", InterfaceModule.FontType.PRODUCT_SANS);
      this.lowerCaseProperty = new BooleanProperty("Lower Case", false);
      this.fpsProperty = new BooleanProperty("FPS", true);
      this.bpsProperty = new BooleanProperty("BPS", true);
      this.primaryColorProperty = new ColorProperty("Primary Color", new Color(7, 236, 253));
      this.secondaryColorProperty = new ColorProperty("Secondary Color", new Color(0, 142, 250));
      this.addProperties(new Property[]{this.fontTypeProperty, this.lowerCaseProperty, this.primaryColorProperty, this.secondaryColorProperty, this.fpsProperty, this.bpsProperty});
      Ethane.getInstance().getDraggableRepository().register(new FPSDraggableElement());
      Ethane.getInstance().getDraggableRepository().register(new BPSDraggableElement());
      this.font = MgfxFontRepository.fetch("product_sans_bold", 10.0F);
   }

   @Subscribe
   public void onRender(Render2DEvent event) {
      if (!this.mc.field_1690.field_1842) {
         double mouseX = this.mc.field_1729.method_1603() * (double)this.mc.method_22683().method_4486() / (double)this.mc.method_22683().method_4480();
         double mouseY = this.mc.field_1729.method_1604() * (double)this.mc.method_22683().method_4502() / (double)this.mc.method_22683().method_4507();
         Iterator var6 = Ethane.getInstance().getDraggableRepository().getDraggables().iterator();

         while(true) {
            DraggableComponent draggable;
            do {
               do {
                  if (!var6.hasNext()) {
                     float baseX = 5.0F;
                     int color1 = ColorUtil.getWaveColor(((ColorProperty)this.primaryColorProperty).getRGB(), ((ColorProperty)this.secondaryColorProperty).getRGB(), (double)baseX);
                     int color2 = ColorUtil.getWaveColor(((ColorProperty)this.primaryColorProperty).getRGB(), ((ColorProperty)this.secondaryColorProperty).getRGB(), (double)(baseX + this.font.getFont().width((Boolean)this.lowerCaseProperty.getValue() ? "ethane" : "Ethane", 15.5F) - 5.0F));
                     if (this.fontTypeProperty.getValue() == InterfaceModule.FontType.MINECRAFT) {
                        event.getGraphics().method_51448().pushMatrix();
                        event.getGraphics().method_51448().scale(1.5F, 1.5F);
                        event.getGraphics().method_25303(this.mc.field_1772, (Boolean)this.lowerCaseProperty.getValue() ? "Trollhack" : "TROLLHACK", 5, 5, color1);
                        event.getGraphics().method_51448().popMatrix();
                     } else {
                        float y = 5.0F;
                        switch(((InterfaceModule.FontType)this.fontTypeProperty.getValue()).ordinal()) {
                        case 0:
                           event.getContext().drawFont(Msdf.builder().font(this.font.getFont()).text((Boolean)this.lowerCaseProperty.getValue() ? "Trollhack" : "TROLLHACK").size(15.5F).xy(baseX + 2.0F, y).paint(MgfxPaint.builder().gradient(color1, color2, baseX + 2.0F, y, baseX + 2.0F + this.font.getFont().width((Boolean)this.lowerCaseProperty.getValue() ? "ethane" : "Ethane", 15.5F), y + 15.5F)));
                        }
                     }

                     return;
                  }

                  draggable = (DraggableComponent)var6.next();
               } while(draggable instanceof FPSDraggableElement && !(Boolean)this.fpsProperty.getValue());
            } while(draggable instanceof BPSDraggableElement && !(Boolean)this.bpsProperty.getValue());

            draggable.render(event.getGraphics(), (int)mouseX, (int)mouseY, event.getDelta());
         }
      }
   }

   @Subscribe
   public void onRenderBloom(RenderBloomEvent event) {
      if (!this.mc.field_1690.field_1842) {
         Iterator var2 = Ethane.getInstance().getDraggableRepository().getDraggables().iterator();

         while(true) {
            DraggableComponent draggable;
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  draggable = (DraggableComponent)var2.next();
               } while(draggable instanceof FPSDraggableElement && !(Boolean)this.fpsProperty.getValue());
            } while(draggable instanceof BPSDraggableElement && !(Boolean)this.bpsProperty.getValue());

            draggable.renderBloom(event.getGraphics(), event.getDelta());
         }
      }
   }

   public String getSuffix() {
      return ((InterfaceModule.FontType)this.fontTypeProperty.getValue()).toString();
   }

   public static enum FontType {
      PRODUCT_SANS("Product Sans"),
      MINECRAFT("Minecraft");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private FontType(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static InterfaceModule.FontType[] $values() {
         return new InterfaceModule.FontType[]{PRODUCT_SANS, MINECRAFT};
      }
   }
}
