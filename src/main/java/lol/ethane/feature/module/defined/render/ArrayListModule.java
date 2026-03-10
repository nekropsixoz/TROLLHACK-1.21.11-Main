package lol.ethane.feature.module.defined.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.ethane.Ethane;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.utils.render.ColorUtil;
import lol.ethane.utils.render.animation.translation.ModuleAnimation;
import lombok.Generated;

public class ArrayListModule extends Module {
   public static ArrayListModule INSTANCE;
   public final Property<ArrayListModule.Theme> themeProperty;
   private final Property<Boolean> fontShadowProperty;
   private final Property<Boolean> lowerCaseProperty;
   private static final Map<String, ModuleAnimation> animationMap = new HashMap();
   private static int lastWidth;
   private static int lastHeight;
   private final List<Module> sortedModules;
   private boolean needsSort;
   private static final int BG_COLOR_2 = (new Color(0, 0, 0, 140)).getRGB();
   private MgfxFont font;

   public ArrayListModule() {
      super("Array List", "Displays a list of enabled modules.", ModuleCategory.RENDER);
      this.themeProperty = new EnumProperty("Theme", ArrayListModule.Theme.BACKGROUND);
      this.fontShadowProperty = (new BooleanProperty("Font Shadow", false)).hideIf(() -> {
         if (Ethane.getInstance().getModuleRepository() == null) {
            return false;
         }
         InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
         return interfaceModule.fontTypeProperty.getValue() != InterfaceModule.FontType.MINECRAFT;
      });
      this.lowerCaseProperty = new BooleanProperty("Lower Case", false);
      this.sortedModules = new ArrayList();
      this.needsSort = true;
      INSTANCE = this;
      this.addProperties(new Property[]{this.themeProperty, this.fontShadowProperty, this.lowerCaseProperty});
   }
   
   private MgfxFont getFont() {
      if (this.font == null) {
         this.font = MgfxFontRepository.fetch("product_sans_bold", 9.0F);
      }
      return this.font;
   }

   private ModuleAnimation getAnimation(Module module) {
      return (ModuleAnimation)animationMap.computeIfAbsent(module.getId(), (id) -> {
         return new ModuleAnimation((double)(this.mc.method_22683().method_4486() + 10), 0.0D);
      });
   }

   private void updateSortedModules(InterfaceModule interfaceModule) {
      if (this.needsSort) {
         this.sortedModules.clear();
         Iterator var2 = Ethane.getInstance().getModuleRepository().getModules().iterator();

         while(var2.hasNext()) {
            Module m = (Module)var2.next();
            if (m != this) {
               this.sortedModules.add(m);
            }
         }

         this.sortedModules.sort(Comparator.comparingDouble((mx) -> {
            return (double)this.getWidth((Module)mx, interfaceModule);
         }).reversed());
         this.needsSort = false;
      }

   }

   @Subscribe
   private void onRender2D(Render2DEvent e) {
      InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
      this.handleResize();
      this.updateSortedModules(interfaceModule);
      float y = 7.5F;
      boolean first = true;
      int primaryColor = ((ColorProperty)interfaceModule.primaryColorProperty).getRGB();
      int secondaryColor = ((ColorProperty)interfaceModule.secondaryColorProperty).getRGB();
      Iterator var7 = this.sortedModules.iterator();

      while(true) {
         while(var7.hasNext()) {
            Module module = (Module)var7.next();
            ModuleAnimation anim = this.getAnimation(module);
            boolean enabled = module.isEnabled();
            if (!enabled && anim.getX() >= (double)((float)this.mc.method_22683().method_4486() + 4.0F)) {
               anim.setY((double)y);
            } else {
               float width = this.getWidth(module, interfaceModule);
               if (enabled) {
                  if (anim.getX() >= (double)this.mc.method_22683().method_4486()) {
                     anim.setY((double)y);
                  }

                  anim.animate((double)((float)this.mc.method_22683().method_4486() - width - 9.5F), (double)y);
               } else {
                  anim.animate((double)((float)this.mc.method_22683().method_4486() + 5.0F), (double)y);
               }

               float currentY = (float)anim.getY();
               e.getContext().drawRectangle(Rectangle.builder().xywh((float)anim.getX(), currentY, width + 4.0F, 12.0F).color(BG_COLOR_2));
               int waveColor = ColorUtil.getWaveColor(primaryColor, secondaryColor, (double)y);
               switch(((ArrayListModule.Theme)this.themeProperty.getValue()).ordinal()) {
               case 1:
                  e.getContext().drawRectangle(Rectangle.builder().xywh((float)anim.getX() + width + 3.8F, currentY + 1.5F, 2.0F, 9.0F).color(waveColor).radius(0.0F, 2.5F, 2.5F, 0.0F));
                  break;
               case 2:
                  e.getContext().drawRectangle(Rectangle.builder().xywh((float)anim.getX() + width + 3.8F, currentY, 1.0F, 12.0F).color(waveColor));
                  if (first) {
                     e.getContext().drawRectangle(Rectangle.builder().xywh((float)anim.getX(), currentY, width + 4.0F, 1.0F).color(waveColor));
                  }
               }

               String name = module.getName();
               String suffix = module.getSuffix();
               if ((Boolean)this.lowerCaseProperty.getValue()) {
                  name = name.toLowerCase();
                  if (suffix != null) {
                     suffix = suffix.toLowerCase();
                  }
               }

               if (interfaceModule.fontTypeProperty.getValue() == InterfaceModule.FontType.MINECRAFT) {
                  e.getGraphics().method_51433(this.mc.field_1772, name, (int)(anim.getX() + 3.0D), (int)((double)currentY + 2.5D), waveColor, (Boolean)this.fontShadowProperty.getValue());
                  if (module.getSuffix() != null) {
                     e.getGraphics().method_51433(this.mc.field_1772, "- " + suffix, (int)(anim.getX() + 3.0D + (double)this.mc.field_1772.method_1727(" " + name)), (int)((double)currentY + 2.5D), (new Color(211, 211, 211)).getRGB(), (Boolean)this.fontShadowProperty.getValue());
                  }
               } else {
                  e.getContext().drawFont(Msdf.builder().font(this.getFont().getFont()).text(name).xy((float)(anim.getX() + 2.0D), currentY + 0.5F).size(9.0F).color(waveColor));
                  if (module.getSuffix() != null) {
                     e.getContext().drawFont(Msdf.builder().font(this.getFont().getFont()).text("- " + suffix).xy((float)(anim.getX() + 2.0D + (double)this.getFont().getFont().width(" " + name, 9.0F)), currentY + 0.5F).size(9.0F).color(new Color(211, 211, 211)));
                  }
               }

               y += 12.0F;
               first = false;
            }
         }

         return;
      }
   }

   private void handleResize() {
      int width = this.mc.method_22683().method_4486();
      int height = this.mc.method_22683().method_4502();
      if (lastWidth != width || lastHeight != height) {
         if (lastWidth != 0) {
            int deltaX = width - lastWidth;
            int deltaY = height - lastHeight;
            Iterator var5 = animationMap.values().iterator();

            while(var5.hasNext()) {
               ModuleAnimation anim = (ModuleAnimation)var5.next();
               anim.setX(anim.getX() + (double)deltaX);
               anim.setY(anim.getY() + (double)deltaY);
            }
         }

         lastWidth = width;
         lastHeight = height;
      }

   }

   private float getWidth(Module module, InterfaceModule interfaceModule) {
      String name = module.getSuffix() == null ? module.getName() : module.getName() + " - " + module.getSuffix();
      if ((Boolean)this.lowerCaseProperty.getValue()) {
         name = name.toLowerCase();
      }

      return interfaceModule.fontTypeProperty.getValue() == InterfaceModule.FontType.MINECRAFT ? (float)this.mc.field_1772.method_1727(name) : this.getFont().getFont().width(name, 9.0F);
   }

   public void onEnable() {
      super.onEnable();
      this.needsSort = true;
   }

   public void setNeedsSort() {
      this.needsSort = true;
   }

   public static enum Theme {
      BACKGROUND("Background"),
      BAR("Bar"),
      BORDER("Border");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private Theme(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static ArrayListModule.Theme[] $values() {
         return new ArrayListModule.Theme[]{BACKGROUND, BAR, BORDER};
      }
   }
}
