package lol.ethane.click.dropdown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lol.aether.MgfxQueueRenderer;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.click.dropdown.components.CategoryComponent;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.defined.render.ClickGUIModule;
import lol.ethane.utils.misc.Stopwatch;
import lol.ethane.utils.render.animation.Animation;
import lol.ethane.utils.render.animation.Easing;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jspecify.annotations.NonNull;

public class DropdownClickGUI extends class_437 {
   public final List<CategoryComponent> categories = new ArrayList();
   private final Stopwatch stopwatch = new Stopwatch();
   private boolean closing;
   private static String pendingTooltip;
   private static float pendingX;
   private static float pendingY;
   private String activeTooltip;
   private float activeX;
   private float activeY;
   private final Animation tooltipAnimation;

   public DropdownClickGUI() {
      super(class_2561.method_43473());
      this.tooltipAnimation = new Animation(Easing.LINEAR, 200L);
   }

   protected void method_25426() {
      this.activeTooltip = null;
      this.closing = false;
      this.categories.clear();
      float inset = 10.0F;
      float categoryWidth = 90.0F;
      float categoryHeight = 17.0F;
      float totalWidth = (float)ModuleCategory.values().length * 100.0F - 10.0F;
      int i = 0;
      ModuleCategory[] var6 = ModuleCategory.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         ModuleCategory type = var6[var8];
         CategoryComponent element = new CategoryComponent(type);
         float categoryX = (float)this.field_22789 / 2.0F - totalWidth / 2.0F + (float)i * 100.0F;
         element.index = i;
         element.setBounds(categoryX, 60.0F, 90.0F, 17.0F);
         this.categories.add(element);
         ++i;
      }

      this.stopwatch.reset();
   }

   public void exit() {
      if (!this.closing) {
         this.closing = true;
         this.stopwatch.reset();
      }

   }

   public void method_25420(@NonNull class_332 guiGraphics, int i, int j, float f) {
   }

   public void method_25394(@NonNull class_332 guiGraphics, int mouseX, int mouseY, float delta) {
      if (!ClickGUIModule.drawingBloom) {
         pendingTooltip = null;
      }

      MgfxQueueRenderer.request(() -> {
         this.renderInternal(mouseX, mouseY);
      });
   }

   private void renderInternal(int mouseX, int mouseY) {
      boolean allFinished = true;
      MgfxContext context = Ethane.getInstance().getRenderer().getContext();

      for(int i = 0; i < this.categories.size(); ++i) {
         CategoryComponent element = (CategoryComponent)this.categories.get(i);
         if (this.closing) {
            element.animation.setEasing(Easing.IN_BACK);
            element.animation.setDuration(250L);
            if (this.stopwatch.elapsed((long)i * 50L)) {
               element.animation.process(0.0D);
            } else {
               allFinished = false;
            }

            if (!element.animation.isFinished() || element.animation.getValue() > 0.0D) {
               allFinished = false;
            }
         } else {
            element.animation.setEasing(Easing.OUT_QUART);
            element.animation.setDuration(500L);
            if (this.stopwatch.elapsed((long)i * 50L)) {
               element.animation.process(1.0D);
            }

            allFinished = false;
         }

         float scale = (float)element.animation.getValue();
         if (!(scale <= 0.0F)) {
            context.push();
            context.translate((float)this.field_22789 / 2.0F * context.getScale(), (float)this.field_22790 / 2.0F * context.getScale());
            context.scale(scale);
            context.translate(-((float)this.field_22789 / 2.0F * context.getScale()), -((float)this.field_22790 / 2.0F * context.getScale()));
            element.render(context, mouseX, mouseY);
            context.pop();
         }
      }

      if (this.closing && allFinished) {
         this.field_22787.method_1507((class_437)null);
         ((ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class)).setEnabled(false);
      }

   }

   public static void requestTooltip(String text, float x, float y) {
      pendingTooltip = text;
      pendingX = x;
      pendingY = y;
   }

   public boolean method_25404(class_11908 event) {
      if (event.comp_4795() == 256 && !this.closing) {
         this.closing = true;
         this.stopwatch.reset();
         return true;
      } else {
         if (event.comp_4795() == 256) {
            ((ClickGUIModule)Ethane.getInstance().getModuleRepository().getModule(ClickGUIModule.class)).setEnabled(false);
         }

         return super.method_25404(event);
      }
   }

   public boolean method_25421() {
      return false;
   }

   public boolean method_25402(@NonNull class_11909 event, boolean bl) {
      Iterator var3 = this.categories.iterator();

      while(var3.hasNext()) {
         CategoryComponent element = (CategoryComponent)var3.next();
         element.mouseClicked(event);
      }

      return super.method_25402(event, bl);
   }

   public boolean method_25406(@NonNull class_11909 event) {
      Iterator var2 = this.categories.iterator();

      while(var2.hasNext()) {
         CategoryComponent element = (CategoryComponent)var2.next();
         element.mouseReleased(event);
      }

      return super.method_25406(event);
   }
}
