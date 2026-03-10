package lol.ethane.click.dropdown.components.properties;

import java.awt.Color;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.dropdown.components.ModuleComponent;
import lol.ethane.click.framework.Component;
import lol.ethane.click.framework.PropertyComponent;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.utils.render.ColorUtil;
import net.minecraft.class_11909;

public class ColorPropertyComponent extends PropertyComponent<ColorProperty> {
   private boolean draggingHue;
   private boolean draggingSB;
   private float hue;
   private float renderHue;
   private float renderSaturation;
   private float renderBrightness;
   private final String name;
   private final String lowerCaseName;
   private final float[] hsbCache = new float[3];
   private final MgfxFont font;

   public ColorPropertyComponent(Component parent, ColorProperty value) {
      super(value);
      this.parent = parent;
      this.name = value.getName();
      this.lowerCaseName = value.getName().toLowerCase();
      Color color = (Color)value.getValue();
      Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), this.hsbCache);
      this.hue = this.hsbCache[0];
      this.renderHue = this.hue;
      this.renderSaturation = this.hsbCache[1];
      this.renderBrightness = this.hsbCache[2];
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

      context.push();
      context.translate(this.x * context.getScale(), (this.parent.y + this.parent.height) * context.getScale());
      context.scale(expansion);
      context.translate(-(this.x * context.getScale()), -((this.parent.y + this.parent.height) * context.getScale()));
      context.drawFont(Msdf.builder().font(this.font.getFont()).text((Boolean)guiModule.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name).xy(this.x + 2.5F, this.y + 1.5F).size(this.font.getSize()).color(ColorUtil.applyOpacity(-1, expansion)));
      context.pop();
      float previewWidth = 17.5F;
      context.push();
      context.translate((this.x + this.width) * context.getScale(), (this.parent.y + this.parent.height) * context.getScale());
      context.scale(expansion);
      context.translate(-((this.x + this.width) * context.getScale()), -((this.parent.y + this.parent.height) * context.getScale()));
      context.drawRectangle(Rectangle.builder().xywh(this.x + this.width - 17.5F - 2.5F, this.y + 1.5F, 17.5F, 8.0F).radius(4.0F).color(ColorUtil.applyOpacity(((Color)((ColorProperty)this.getValue()).getValue()).getRGB(), expansion)));
      context.pop();
      if (this.expanded) {
         context.push();
         context.translate((this.x + this.width / 2.0F) * context.getScale(), (this.parent.y + this.parent.height) * context.getScale());
         context.scale(expansion);
         context.translate(-((this.x + this.width / 2.0F) * context.getScale()), -((this.parent.y + this.parent.height) * context.getScale()));
         Color color = (Color)((ColorProperty)this.getValue()).getValue();
         Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), this.hsbCache);
         float sbBoxX = this.x + 2.5F;
         float sbBoxY = this.y + 15.0F;
         float sbBoxWidth = this.width - 5.0F;
         float sbBoxHeight = 40.0F;
         float sbInset = 3.0F;
         float sbClickableWidth = sbBoxWidth - 6.0F;
         float sbClickableHeight = 34.0F;
         float sbPickerX;
         float sbPickerY;
         float hueBarY;
         float hueBarHeight;
         if (this.draggingSB) {
            sbPickerX = Math.max(0.0F, Math.min(sbClickableWidth, (float)mouseX - (sbBoxX + 3.0F)));
            sbPickerY = Math.max(0.0F, Math.min(34.0F, (float)mouseY - (sbBoxY + 3.0F)));
            hueBarY = sbPickerX / sbClickableWidth;
            hueBarHeight = 1.0F - sbPickerY / 34.0F;
            ((ColorProperty)this.getValue()).setValue(new Color(Color.HSBtoRGB(this.hue, hueBarY, hueBarHeight)));
         }

         context.drawRectangle(Rectangle.builder().xywh(sbBoxX, sbBoxY, sbBoxWidth, 40.0F).radius(6.0F).paint(MgfxPaint.builder().horizontalGradient(ColorUtil.applyOpacity(-1, expansion), ColorUtil.applyOpacity(Color.HSBtoRGB(this.hue, 1.0F, 1.0F), expansion))));
         context.drawRectangle(Rectangle.builder().xywh(sbBoxX - 0.25F, sbBoxY - 0.25F, sbBoxWidth + 0.5F, 40.5F).radius(6.0F).paint(MgfxPaint.builder().verticalGradient(ColorUtil.applyOpacity(-16777216, 0), ColorUtil.applyOpacity(-16777216, expansion))));
         this.renderSaturation += (this.hsbCache[1] - this.renderSaturation) * 0.2F;
         this.renderBrightness += (this.hsbCache[2] - this.renderBrightness) * 0.2F;
         sbPickerX = sbBoxX + 3.0F + this.renderSaturation * sbClickableWidth;
         sbPickerY = sbBoxY + 3.0F + (1.0F - this.renderBrightness) * 34.0F;
         context.drawRectangle(Rectangle.builder().xywh(sbPickerX - 0.5F, sbPickerY - 0.5F, 4.0F, 4.0F).radius(1.5F).color(-16777216));
         context.drawRectangle(Rectangle.builder().xywh(sbPickerX, sbPickerY, 3.0F, 3.0F).radius(1.5F).color(((ColorProperty)this.getValue()).getRGB()));
         hueBarY = sbBoxY + 40.0F + 5.0F;
         hueBarHeight = 5.0F;
         float hueBarWidth = this.width - 5.0F;
         float hueBarX = this.x + 2.5F;
         float huePickerX;
         if (this.draggingHue) {
            huePickerX = Math.max(0.0F, Math.min(hueBarWidth, (float)mouseX - hueBarX));
            this.hue = huePickerX / hueBarWidth;
            ((ColorProperty)this.getValue()).setValue(new Color(Color.HSBtoRGB(this.hue, this.hsbCache[1], this.hsbCache[2])));
         }

         this.renderHue += (this.hue - this.renderHue) * 0.2F;
         huePickerX = hueBarX + this.renderHue * hueBarWidth;
         float huePickerY = hueBarY + 2.5F;
         context.drawRectangle(Rectangle.builder().xywh(huePickerX - 0.5F, huePickerY - 0.5F, 4.0F, 4.0F).radius(1.5F).color(-16777216));
         context.drawRectangle(Rectangle.builder().xywh(huePickerX, huePickerY, 3.0F, 3.0F).radius(1.5F).color(((ColorProperty)this.getValue()).getRGB()));
         context.pop();
      }
   }

   public void mouseClicked(class_11909 event) {
      try {
         float sbBoxX = this.x + 2.5F;
         float sbBoxY = this.y + 15.0F;
         float sbBoxWidth = this.width - 5.0F;
         float sbBoxHeight = 40.0F;
         float hueBarY = sbBoxY + 40.0F + 5.0F;
         float hueBarHeight = 5.0F;
         float hueBarWidth = this.width - 5.0F;
         float hueBarX = this.x + 2.5F;
         if (event.method_74245() == 0) {
            if (this.expanded) {
               if (this.isHovered((double)hueBarX, (double)hueBarY, (double)hueBarWidth, 5.0D, event.comp_4798(), event.comp_4799())) {
                  this.draggingHue = true;
               } else if (this.isHovered((double)sbBoxX, (double)sbBoxY, (double)sbBoxWidth, 40.0D, event.comp_4798(), event.comp_4799())) {
                  this.draggingSB = true;
               } else if (this.isHovered((double)this.x, (double)this.y, (double)this.width, 15.0D, event.comp_4798(), event.comp_4799())) {
                  this.expanded = false;
               }
            } else if (this.isHovered((double)this.x, (double)this.y, (double)this.width, 15.0D, event.comp_4798(), event.comp_4799())) {
               this.expanded = true;
            }
         }

      } catch (Throwable var10) {
         throw var10;
      }
   }

   public void mouseReleased(class_11909 event) {
      if (event.method_74245() == 0) {
         this.draggingHue = false;
         this.draggingSB = false;
      }

   }

   public float getTotalHeight() {
      return this.expanded ? 67.0F : 14.0F;
   }
}
