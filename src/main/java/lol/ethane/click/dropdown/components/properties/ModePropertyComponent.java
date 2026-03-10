package lol.ethane.click.dropdown.components.properties;

import lol.aether.builders.Msdf;
import lol.aether.font.MgfxFont;
import lol.aether.font.MgfxFontRepository;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.dropdown.components.ModuleComponent;
import lol.ethane.click.framework.Component;
import lol.ethane.click.framework.Palette;
import lol.ethane.click.framework.PropertyComponent;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.utils.misc.SoundUtil;
import lol.ethane.utils.misc.StringUtil;
import lol.ethane.utils.render.ColorUtil;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import net.minecraft.class_11909;

public class ModePropertyComponent extends PropertyComponent<ModeProperty<?>> {
   private final Animation animation;
   private int click;
   private final String name;
   private final String lowerCaseName;
   private final String[] modeNames;
   private final float[] modeWidths;
   private final MgfxFont font;

   public ModePropertyComponent(Component parent, ModeProperty<?> value) {
      super(value);
      this.animation = new Animation(Easing.DECELERATE, 250L);
      this.parent = parent;
      this.name = value.getName();
      this.lowerCaseName = value.getName().toLowerCase();
      Enum<?>[] values = value.getValues();
      this.modeNames = new String[values.length];
      this.modeWidths = new float[values.length];
      this.font = MgfxFontRepository.fetch("product_sans_bold", 6.5F);

      for(int i = 0; i < values.length; ++i) {
         this.modeNames[i] = StringUtil.normalizeEnumName(values[i].toString());
         this.modeWidths[i] = this.font.getFont().width(this.modeNames[i], 6.5F);
      }

   }

   public void render(MgfxContext context, int mouseX, int mouseY) {
      ClickGUIModule guiModule = (ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class);
      float expansion = 1.0F;
      Component var7 = this.parent;
      if (var7 instanceof ModuleComponent) {
         ModuleComponent moduleComponent = (ModuleComponent)var7;
         expansion = moduleComponent.getExpansionProgress();
      }

      this.animation.process((double)(11 * ((Enum)((ModeProperty)this.getValue()).getValue()).ordinal()));
      float guiScale = context.getScale();
      context.push();
      context.translate(this.x * guiScale, (this.parent.y + this.parent.height) * guiScale);
      context.scale(expansion);
      context.translate(-this.x * guiScale, -(this.parent.y + this.parent.height) * guiScale);
      context.drawFont(Msdf.builder().font(this.font.getFont()).text((Boolean)guiModule.lowerCaseProperty.getValue() ? this.lowerCaseName : this.name).xy(this.x + 2.5F, this.y + 1.5F).size(this.font.getSize()).color(ColorUtil.applyOpacity(-1, expansion)));
      context.pop();
      context.push();
      context.translate((this.x + this.width) * guiScale, (this.parent.y + this.parent.height) * guiScale);
      context.scale(expansion);
      context.translate(-(this.x + this.width) * guiScale, -(this.parent.y + this.parent.height) * guiScale);
      context.startScissor(this.x, this.y, this.width, this.height);

      for(int i = 0; i < this.modeNames.length; ++i) {
         context.drawFont(Msdf.builder().font(this.font.getFont()).text(this.modeNames[i]).xy(this.x + this.width - this.modeWidths[i] - 2.5F, (float)((double)(this.y + 1.5F + (float)(i * 11)) - this.animation.getValue())).size(this.font.getSize()).color(ColorUtil.applyOpacity(Palette.PROPERTY_KNOB_DISABLED_COLOR, expansion)));
      }

      context.endScissor();
      context.pop();
   }

   public void mouseClicked(class_11909 event) {
      try {
         if (this.isHovered(event.comp_4798(), event.comp_4799()) && (event.method_74245() == 0 || event.method_74245() == 1)) {
            ((ModeProperty)this.getValue()).cycle(event.method_74245() == 0);
            SoundUtil.play(this.click++ % 2 == 0 ? "enable" : "disable");
         }

      } catch (Throwable var3) {
         throw var3;
      }
   }

   public float getTotalHeight() {
      return 14.0F;
   }
}
