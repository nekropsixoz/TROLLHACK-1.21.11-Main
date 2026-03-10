package lol.ethane.click.dropdown.components.properties;

import java.awt.Color;
import java.text.DecimalFormat;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.dropdown.components.ModuleComponent;
import lol.ethane.click.framework.Component;
import lol.ethane.click.framework.Palette;
import lol.ethane.click.framework.PropertyComponent;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.defined.render.InterfaceModule;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.math.MathUtil;
import lol.ethane.utils.render.ColorUtil;
import net.minecraft.class_11909;

public class NumberPropertyComponent extends PropertyComponent<NumberProperty> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
   private boolean dragging;
   private double renderProgress;
   private final String name;
   private final String lowerCaseName;
   private final MgfxFont font;

   public NumberPropertyComponent(Component parent, NumberProperty value) {
      super(value);
      this.parent = parent;
      this.name = value.getName();
      this.lowerCaseName = value.getName().toLowerCase();
      this.font = MgfxFontRepository.fetch("product_sans_bold", 6.5F);
   }

   public void render(MgfxContext context, int mouseX, int mouseY) {
      ClickGUIModule guiModule = (ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class);
      float expansion = 1.0F;
      Component var7 = this.parent;
      if (var7 instanceof ModuleComponent) {
         ModuleComponent moduleComponent = (ModuleComponent)var7;
         expansion = moduleComponent.getExpansionProgress();
      }

      String valueString = FORMAT.format(((NumberProperty)this.getValue()).getValue());
      if (((NumberProperty)this.getValue()).getSuffix() != null) {
         valueString = valueString + " " + ((NumberProperty)this.getValue()).getSuffix();
      }

      float valueWidth = this.font.getFont().width(valueString, 6.5F);
      context.push();
      context.translate((this.x + this.width) * context.getScale(), (this.parent.y + this.parent.height) * context.getScale());
      context.scale(expansion);
      context.translate(-((this.x + this.width) * context.getScale()), -((this.parent.y + this.parent.height) * context.getScale()));
      context.drawFont(Msdf.builder().font(this.font.getFont()).text(valueString).xy(this.x + this.width - valueWidth - 2.5F, this.y + 1.0F).size(this.font.getSize()).color(ColorUtil.applyOpacity(Palette.PROPERTY_TEXT_SECONDARY_COLOR, expansion)));
      context.pop();
      context.push();
      context.translate(this.x * context.getScale(), (this.parent.y + this.parent.height) * context.getScale());
      context.scale(expansion);
      context.translate(-(this.x * context.getScale()), -((this.parent.y + this.parent.height) * context.getScale()));
      context.drawFont(Msdf.builder().font(this.font.getFont()).text((Boolean)guiModule.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name).xy(this.x + 2.5F, this.y + 1.0F).size(this.font.getSize()).color(ColorUtil.applyOpacity(-1, expansion)));
      float sliderX = this.x + 2.5F;
      float sliderY = this.y + 12.0F;
      float sliderWidth = this.width - 5.0F;
      float sliderHeight = 2.5F;
      float knobSize = 4.0F;
      double min = ((NumberProperty)this.getValue()).getMinValue();
      double max = ((NumberProperty)this.getValue()).getMaxValue();
      double value = (Double)((NumberProperty)this.getValue()).getValue();
      double range = max - min;
      double targetProgress = (value - min) / range;
      if (this.dragging) {
         float mouseProgress = ((float)mouseX - sliderX) / sliderWidth;
         mouseProgress = Math.min(1.0F, Math.max(0.0F, mouseProgress));
         double newValue = min + range * (double)mouseProgress;
         ((NumberProperty)this.getValue()).setValue(MathUtil.roundAndClamp(newValue, min, max, ((NumberProperty)this.getValue()).getIncrement()).doubleValue());
      }

      this.renderProgress += (targetProgress - this.renderProgress) * 0.07500000298023224D;
      context.drawRectangle(Rectangle.builder().xywh(sliderX, sliderY, sliderWidth, 2.5F).radius(1.75F).color(ColorUtil.applyOpacity(Palette.PROPERTY_BACKGROUND_COLOR, expansion)));
      InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
      int primaryColor = ((Color)interfaceModule.primaryColorProperty.getValue()).getRGB();
      int secondaryColor = ((Color)interfaceModule.secondaryColorProperty.getValue()).getRGB();
      int startColor = ColorUtil.getWaveColor(primaryColor, secondaryColor, (double)sliderX);
      int endColor = ColorUtil.getWaveColor(primaryColor, secondaryColor, (double)(sliderX + (float)((double)sliderWidth * this.renderProgress)));
      float knobRadius = 2.0F;
      float availableMovement = sliderWidth - 4.0F;
      float knobX = sliderX + knobRadius + availableMovement * (float)this.renderProgress;
      float knobY = sliderY + 1.25F;
      if (this.renderProgress > 0.0D) {
         float currentFillWidth = knobX - sliderX;
         if (currentFillWidth > 3.0F) {
            context.drawRectangle(Rectangle.builder().xywh(sliderX, sliderY, (float)((double)sliderWidth * this.renderProgress), 2.5F).radius(1.75F).paint(MgfxPaint.builder().horizontalGradient(ColorUtil.applyOpacity(ColorUtil.applyOpacity(startColor, expansion), 0.6F), ColorUtil.applyOpacity(ColorUtil.applyOpacity(endColor, expansion), 0.6F))));
         }
      }

      context.drawRectangle(Rectangle.builder().xywh(knobX - 2.0F, knobY - 2.0F, 4.0F, 4.0F).radius(2.5F).color(ColorUtil.applyOpacity(endColor, expansion)));
      context.pop();
   }

   public void mouseClicked(class_11909 event) {
      try {
         if (event.method_74245() == 0) {
            float sliderX = this.x + 2.5F;
            float sliderY = this.y + 11.0F;
            float sliderWidth = this.width - 5.0F;
            float sliderHeight = 6.0F;
            if (this.isHovered((double)sliderX, (double)(sliderY - 3.0F), (double)sliderWidth, (double)(sliderHeight + 4.0F), event.comp_4798(), event.comp_4799())) {
               this.dragging = true;
            }
         }

      } catch (Throwable var6) {
         throw var6;
      }
   }

   public void mouseReleased(class_11909 event) {
      if (event.method_74245() == 0) {
         this.dragging = false;
      }

   }

   public float getTotalHeight() {
      return 16.0F;
   }
}
