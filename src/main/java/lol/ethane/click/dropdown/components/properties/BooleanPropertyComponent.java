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
import lol.ethane.click.framework.Palette;
import lol.ethane.click.framework.PropertyComponent;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.defined.render.InterfaceModule;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.utils.misc.SoundUtil;
import lol.ethane.utils.render.ColorUtil;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import net.minecraft.class_11909;

public class BooleanPropertyComponent extends PropertyComponent<BooleanProperty> {
   private final Animation animation;
   private final Animation color;
   private final String name;
   private final String lowerCaseName;
   private final MgfxFont font;

   public BooleanPropertyComponent(Component parent, BooleanProperty value) {
      super(value);
      this.animation = new Animation(Easing.OUT_ELASTIC, 1000L);
      this.color = new Animation(Easing.SMOOTH, 500L);
      this.parent = parent;
      this.name = value.getName();
      this.lowerCaseName = value.getName().toLowerCase();
      this.font = MgfxFontRepository.fetch("product_sans_bold", 6.5F);
   }

   public void render(MgfxContext context, int mouseX, int mouseY) {
      ClickGUIModule guiModule = (ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class);
      InterfaceModule interfaceModule = (InterfaceModule)Ethane.getInstance().getModuleRepository().getModule(InterfaceModule.class);
      int primaryColor = ((Color)interfaceModule.primaryColorProperty.getValue()).getRGB();
      int secondaryColor = ((Color)interfaceModule.secondaryColorProperty.getValue()).getRGB();
      this.animation.process(((BooleanProperty)this.getValue()).getValue() ? 1.0D : 0.0D);
      this.color.process(((BooleanProperty)this.getValue()).getValue() ? 1.0D : 0.0D);
      float expansion = 1.0F;
      Component var10 = this.parent;
      if (var10 instanceof ModuleComponent) {
         ModuleComponent moduleComponent = (ModuleComponent)var10;
         expansion = moduleComponent.getExpansionProgress();
      }

      float switchWidth = 18.0F;
      float switchHeight = this.height - 5.0F + 2.0F;
      float switchX = this.x + this.width - 18.0F - 2.5F;
      float switchY = this.y + 1.5F;
      float guiScale = context.getScale();
      context.push();
      context.translate((this.x + this.width) * guiScale, (this.parent.y + this.parent.height) * guiScale);
      context.scale(expansion);
      context.translate(-(this.x + this.width) * guiScale, -(this.parent.y + this.parent.height) * guiScale);
      context.drawRectangle(Rectangle.builder().xywh(switchX, switchY, 18.0F, switchHeight).radius(switchHeight / 2.0F + 0.5F).paint(MgfxPaint.builder().horizontalGradient(ColorUtil.applyOpacity(ColorUtil.interpolate(Palette.PROPERTY_BACKGROUND_COLOR, primaryColor, this.color.getValue()), expansion), ColorUtil.applyOpacity(ColorUtil.interpolate(Palette.PROPERTY_BACKGROUND_COLOR, secondaryColor, this.color.getValue()), expansion))));
      float circleSize = 6.0F;
      float circleX = (float)((double)(this.x + this.width - 18.0F - 2.0F) + 9.149999618530273D * this.animation.getValue()) + 1.0F;
      float circleY = this.y + 3.0F;
      context.drawRectangle(Rectangle.builder().xywh(circleX, circleY, 6.0F, 6.0F).radius(3.5F).color(ColorUtil.applyOpacity(ColorUtil.interpolate(Palette.PROPERTY_KNOB_DISABLED_COLOR, -1, this.color.getValue()), expansion)));
      context.pop();
      context.push();
      context.translate(this.x * guiScale, (this.parent.y + this.parent.height) * guiScale);
      context.scale(expansion);
      context.translate(-this.x * guiScale, -(this.parent.y + this.parent.height) * guiScale);
      context.drawFont(Msdf.builder().font(this.font.getFont()).text((Boolean)guiModule.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name).xy(this.x + 2.5F, this.y + 2.25F).size(this.font.getSize()).color(ColorUtil.applyOpacity(-1, expansion)));
      context.pop();
   }

   public void mouseClicked(class_11909 event) {
      try {
         if (this.isHovered(event.comp_4798(), event.comp_4799()) && event.method_74245() == 0) {
            ((BooleanProperty)this.getValue()).toggle();
            SoundUtil.play(((BooleanProperty)this.getValue()).getValue() ? "enable" : "disable");
         }

      } catch (Throwable var3) {
         throw var3;
      }
   }

   public float getTotalHeight() {
      return 14.0F;
   }
}
