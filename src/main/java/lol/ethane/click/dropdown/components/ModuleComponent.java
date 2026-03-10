package lol.ethane.click.dropdown.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.dropdown.DropdownClickGUI;
import lol.ethane.click.dropdown.components.properties.BooleanPropertyComponent;
import lol.ethane.click.dropdown.components.properties.ColorPropertyComponent;
import lol.ethane.click.dropdown.components.properties.EnumPropertyComponent;
import lol.ethane.click.dropdown.components.properties.ModePropertyComponent;
import lol.ethane.click.dropdown.components.properties.NumberPropertyComponent;
import lol.ethane.click.framework.Component;
import lol.ethane.click.framework.Palette;
import lol.ethane.click.framework.PropertyComponent;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.defined.render.InterfaceModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.utils.misc.SoundUtil;
import lol.ethane.utils.render.ColorUtil;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import lombok.Generated;
import net.minecraft.class_11909;

public class ModuleComponent extends Component {
   private final Module module;
   private final CategoryComponent parent;
   private final String name;
   private final String lowerCaseName;
   private final float nameWidth;
   private final float lowerCaseNameWidth;
   private final List<PropertyComponent<?>> propertyComponents;
   private final Animation toggleAnimation;
   private final Animation nameAnimation;
   private final MgfxFont font;

   public ModuleComponent(CategoryComponent parent, Module module) {
      this.toggleAnimation = new Animation(Easing.SMOOTH, 500L);
      this.nameAnimation = new Animation(Easing.OUT_QUART, 400L);
      this.module = module;
      this.parent = parent;
      this.name = module.getName();
      this.lowerCaseName = module.getName().toLowerCase();
      this.font = MgfxFontRepository.fetch("product_sans_bold", 7.5F);
      this.nameWidth = this.font.getFont().width(this.name, 7.5F);
      this.lowerCaseNameWidth = this.font.getFont().width(this.lowerCaseName, 7.5F);
      this.propertyComponents = new ArrayList();
      Iterator var3 = this.module.getPropertyList().iterator();

      while(var3.hasNext()) {
         Property<?> p = (Property)var3.next();
         if (p instanceof BooleanProperty) {
            BooleanProperty booleanProperty = (BooleanProperty)p;
            this.propertyComponents.add(new BooleanPropertyComponent(this, booleanProperty));
         } else if (p instanceof EnumProperty) {
            EnumProperty<?> enumProperty = (EnumProperty)p;
            this.propertyComponents.add(new EnumPropertyComponent(this, enumProperty));
         } else if (p instanceof ModeProperty) {
            ModeProperty<?> modeProperty = (ModeProperty)p;
            this.propertyComponents.add(new ModePropertyComponent(this, modeProperty));
         } else if (p instanceof ColorProperty) {
            ColorProperty colorProperty = (ColorProperty)p;
            this.propertyComponents.add(new ColorPropertyComponent(this, colorProperty));
         } else if (p instanceof NumberProperty) {
            NumberProperty numberProperty = (NumberProperty)p;
            this.propertyComponents.add(new NumberPropertyComponent(this, numberProperty));
         }
      }

   }

   public void render(MgfxContext context, int mouseX, int mouseY) {
      if (this.isHovered((double)this.x, (double)this.y, (double)this.width, (double)this.height, (double)mouseX, (double)mouseY)) {
         DropdownClickGUI.requestTooltip(this.module.getDescription(), (float)mouseX, (float)mouseY);
      }

      this.toggleAnimation.setDuration(250L);
      this.toggleAnimation.process(this.module.isEnabled() ? 1.0D : 0.0D);
      this.nameAnimation.process(this.expanded ? 1.0D : 0.0D);
      if (this.last) {
         context.drawRectangle(Rectangle.builder().xywh(this.x - 0.3F, this.y - 0.4F, this.width + 0.6F, this.height + 0.8F).color(Palette.MODULE_BACKGROUND).radius(0.0F, 0.0F, 7.0F, 7.0F));
      } else {
         context.drawRectangle(Rectangle.builder().xywh(this.x - 0.3F, this.y - 0.4F, this.width + 0.6F, this.height + 0.8F).color(Palette.MODULE_BACKGROUND));
      }

      if (this.toggleAnimation.getValue() > 0.0D) {
         int alpha = (int)(255.0D * this.toggleAnimation.getValue());
         InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
         int primaryColor = ((ColorProperty)interfaceModule.primaryColorProperty).getRGB();
         int secondaryColor = ((ColorProperty)interfaceModule.secondaryColorProperty).getRGB();
         int startWaveColor = ColorUtil.getWaveColor(primaryColor, secondaryColor, (double)this.parent.index * 20.0D);
         int endWaveColor = ColorUtil.getWaveColor(primaryColor, secondaryColor, (double)(this.parent.index + 1) * 20.0D);
         int startColor = startWaveColor & 16777215 | alpha << 24;
         int endColor = endWaveColor & 16777215 | alpha << 24;
         if (this.last) {
            context.drawRectangle(Rectangle.builder().xywh(this.x - 0.6F, this.y - 0.75F, this.width + 1.3F, this.height + 1.35F).paint(MgfxPaint.builder().horizontalGradient(startColor, endColor)).radius(0.0F, 0.0F, 7.0F, 7.0F));
         } else {
            context.drawRectangle(Rectangle.builder().xywh(this.x - 0.6F, this.y - 0.75F, this.width + 1.3F, this.height + 0.22F).paint(MgfxPaint.builder().horizontalGradient(startColor, endColor)));
         }
      }

      ClickGUIModule module = (ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class);
      String drawName = (Boolean)module.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name;
      float drawWidth = (Boolean)module.lowerCaseProperty.getValue() ? this.lowerCaseNameWidth : this.nameWidth;
      context.drawFont(Msdf.builder().font(this.font.getFont()).text(drawName).xy((float)((double)(this.x + 5.0F) + (double)(this.width / 2.0F - drawWidth / 2.0F - 5.0F) * (1.0D - this.nameAnimation.getValue())), this.y + this.height / 2.0F - this.font.getFont().lineHeight(8.0F) / 2.0F).size(this.font.getSize()).color(ColorUtil.interpolate(Palette.DISABLED_MODULE.getRGB(), Palette.ENABLED_MODULE.getRGB(), this.toggleAnimation.getValue())));
      if (this.nameAnimation.getValue() > 0.0D) {
         float elementX = this.x + 1.0F;
         float elementY = this.y + this.height + 2.0F;
         float elementWidth = this.width - 2.0F;
         Iterator var18 = this.propertyComponents.iterator();

         while(var18.hasNext()) {
            PropertyComponent<?> element = (PropertyComponent)var18.next();
            if (!element.getValue().isHidden()) {
               element.setBounds(elementX, elementY, elementWidth, element.getTotalHeight());
               element.render(context, mouseX, mouseY);
               elementY += element.getTotalHeight() + 1.0F;
            }
         }
      }

   }

   public float getExpansionProgress() {
      return (float)this.nameAnimation.getValue();
   }

   public float getTotalHeight() {
      float totalHeight = 0.0F;
      if (this.nameAnimation.getValue() > 0.0D) {
         boolean hasVisible = false;
         Iterator var3 = this.propertyComponents.iterator();

         while(var3.hasNext()) {
            PropertyComponent<?> component = (PropertyComponent)var3.next();
            if (!component.getValue().isHidden()) {
               totalHeight += component.getTotalHeight() + 1.0F;
               hasVisible = true;
            }
         }

         if (hasVisible) {
            ++totalHeight;
            totalHeight += 2.0F;
         }
      }

      return this.height + (float)((double)totalHeight * Math.max(0.0D, this.nameAnimation.getValue()));
   }

   public void mouseClicked(class_11909 event) {
      if (this.isHovered(event.comp_4798(), event.comp_4799())) {
         switch(event.method_74245()) {
         case 0:
            this.module.toggle();
            SoundUtil.play(this.module.isEnabled() ? "enable" : "disable");
            break;
         case 1:
            if (!this.module.getPropertyList().isEmpty()) {
               this.expanded = !this.expanded;
               SoundUtil.play(this.expanded ? "disable" : "enable");
            }
         }
      } else if (this.expanded) {
         Iterator var2 = this.propertyComponents.iterator();

         while(var2.hasNext()) {
            PropertyComponent<?> propertyComponent = (PropertyComponent)var2.next();
            if (!propertyComponent.getValue().isHidden()) {
               propertyComponent.mouseClicked(event);
            }
         }
      }

   }

   public void mouseReleased(class_11909 event) {
      if (this.expanded) {
         Iterator var2 = this.propertyComponents.iterator();

         while(var2.hasNext()) {
            PropertyComponent<?> propertyComponent = (PropertyComponent)var2.next();
            propertyComponent.mouseReleased(event);
         }
      }

   }

   @Generated
   public Module getModule() {
      return this.module;
   }
}
