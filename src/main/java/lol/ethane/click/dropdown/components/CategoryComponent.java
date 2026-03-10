package lol.ethane.click.dropdown.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import lol.aether.builders.Msdf;
import lol.aether.builders.Rectangle;
import lol.aether.builders.paint.MgfxPaint;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.framework.Component;
import lol.ethane.click.framework.Palette;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.utils.misc.StringUtil;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import lombok.Generated;
import net.minecraft.class_11909;

public class CategoryComponent extends Component {
   public final List<Component> elements;
   private final ModuleCategory category;
   private final String name;
   private final String lowerCaseName;
   private final float nameWidth;
   private final float lowerCaseNameWidth;
   public int index;
   private ClickGUIModule.Sorting lastSorting;
   public final Animation animation;
   private boolean dragging;
   private float dragX;
   private float dragY;
   private float renderX;
   private float renderY;
   private float tilt;
   private final MgfxFont font;

   public CategoryComponent(ModuleCategory category) {
      this.animation = new Animation(Easing.OUT_QUART, 500L);
      this.category = category;
      this.elements = new ArrayList();
      Iterator var2 = Ethane.getInstance().getModuleRepository().getModulesInCategory(category).iterator();

      while(var2.hasNext()) {
         Module module = (Module)var2.next();
         this.elements.add(new ModuleComponent(this, module));
      }

      this.font = MgfxFontRepository.fetch("product_sans_bold", 10.0F);
      this.name = StringUtil.normalizeEnumName(category.name());
      this.lowerCaseName = category.name().toLowerCase(Locale.ROOT);
      this.nameWidth = this.font.getFont().width(this.name, 10.0F);
      this.lowerCaseNameWidth = this.font.getFont().width(this.lowerCaseName, 10.0F);
      this.renderX = this.x;
      this.renderY = this.y;
   }

   public void render(MgfxContext context, int mouseX, int mouseY) {
      if (this.dragging && !ClickGUIModule.drawingBloom) {
         this.x = (float)mouseX - this.dragX;
         this.y = (float)mouseY - this.dragY;
      }

      float targetTilt = 0.0F;
      float lerp;
      if (this.dragging) {
         lerp = this.x - this.renderX;
         targetTilt = lerp / 1.0F;
      }

      lerp = 0.2F;
      this.renderX += (this.x - this.renderX) * lerp;
      this.renderY += (this.y - this.renderY) * lerp;
      this.tilt += (targetTilt - this.tilt) * 0.06F;
      ClickGUIModule module = (ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class);
      if (this.lastSorting != module.sortingProperty.getValue()) {
         this.lastSorting = (ClickGUIModule.Sorting)module.sortingProperty.getValue();
         this.elements.sort((c1, c2) -> {
            if (c1 instanceof ModuleComponent) {
               ModuleComponent m1 = (ModuleComponent)c1;
               if (c2 instanceof ModuleComponent) {
                  ModuleComponent m2 = (ModuleComponent)c2;
                  String n1 = m1.getModule().getName();
                  String n2 = m2.getModule().getName();
                  if (this.lastSorting == ClickGUIModule.Sorting.LENGTH) {
                     int diff = Integer.compare(n2.length(), n1.length());
                     if (diff != 0) {
                        return diff;
                     }
                  }

                  return n1.compareToIgnoreCase(n2);
               }
            }

            return 0;
         });
      }

      float guiScale = context.getScale();
      context.push();
      context.translate((this.x + this.width / 2.0F) * guiScale, this.y * guiScale);
      context.rotateZ(this.tilt);
      context.translate(-(this.x + this.width / 2.0F) * guiScale, -this.y * guiScale);
      context.drawRectangle(Rectangle.builder().xywh(this.x - 0.3F, this.y, this.width + 0.6F, this.getTotalHeight()).color(Palette.BACKGROUND).radius(7.0F));
      float elementX = this.x;
      float elementY = this.y + this.height;
      float elementWidth = this.width;
      float elementHeight = 15.0F;

      Component element;
      for(Iterator var12 = this.elements.iterator(); var12.hasNext(); elementY += element.getTotalHeight()) {
         element = (Component)var12.next();
         element.setBounds(elementX, elementY, elementWidth, elementHeight);
         element.last = element == this.elements.getLast();
         element.render(context, mouseX, mouseY);
      }

      context.drawRectangle(Rectangle.builder().xywh(this.x - 0.3F, this.y + this.height, this.width + 0.6F, 7.5F).paint(MgfxPaint.builder().verticalGradient(Palette.VAR_BLACK_120, Palette.VAR_BLACK_0)));
      context.drawRectangle(Rectangle.builder().xywh(this.x - 0.3F, this.y, this.width + 0.6F, this.height).color(Palette.BACKGROUND).radius(7.0F, 7.0F, 0.0F, 0.0F));
      String drawName = (Boolean)module.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name;
      float drawWidth = (Boolean)module.lowerCaseProperty.getValue() ? this.lowerCaseNameWidth : this.nameWidth;
      context.drawFont(Msdf.builder().font(this.font.getFont()).text(drawName).xy(this.x + this.width / 2.0F - drawWidth / 2.0F, this.y + this.height / 2.0F - this.font.getFont().lineHeight(10.0F) / 2.0F).size(this.font.getSize()));
      context.pop();
   }

   public float getTotalHeight() {
      float totalHeight = 0.0F;

      Component element;
      for(Iterator var2 = this.elements.iterator(); var2.hasNext(); totalHeight += element.getTotalHeight()) {
         element = (Component)var2.next();
      }

      return this.height + totalHeight;
   }

   public void mouseClicked(class_11909 event) {
      if (event.method_74245() == 0 && this.isHovered((double)this.x, (double)this.y, (double)this.width, (double)this.height, event.comp_4798(), event.comp_4799())) {
         this.dragging = true;
         this.dragX = (float)(event.comp_4798() - (double)this.x);
         this.dragY = (float)(event.comp_4799() - (double)this.y);
      }

      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Component element = (Component)var2.next();
         element.mouseClicked(event);
      }

   }

   public void mouseReleased(class_11909 event) {
      if (event.method_74245() == 0) {
         this.dragging = false;
      }

      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Component element = (Component)var2.next();
         element.mouseReleased(event);
      }

   }

   @Generated
   public ModuleCategory getCategory() {
      return this.category;
   }
}
