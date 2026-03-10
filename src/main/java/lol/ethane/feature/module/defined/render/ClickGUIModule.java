package lol.ethane.feature.module.defined.render;

import lol.ethane.Ethane;
import lol.ethane.click.dropdown.DropdownClickGUI;
import lol.ethane.event.defined.render.RenderBloomEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.binding.type.InputType;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lombok.Generated;
import net.minecraft.class_437;

public class ClickGUIModule extends Module {
   public final Property<ClickGUIModule.Sorting> sortingProperty;
   public final Property<Boolean> lowerCaseProperty;
   public DropdownClickGUI dropdownClickGUI;
   public static boolean drawingBloom;

   public ClickGUIModule() {
      super("Click GUI", "Hi", ModuleCategory.RENDER);
      this.sortingProperty = new EnumProperty("Sorting", ClickGUIModule.Sorting.LENGTH);
      this.lowerCaseProperty = new BooleanProperty("Lower Case", false);
      this.addProperties(new Property[]{this.sortingProperty, this.lowerCaseProperty});
      Ethane.getInstance().getBindRepository().getBindingService().register(344, this, InputType.KEYBOARD);
   }

   protected void onEnable() {
      if (this.mc.field_1724 != null) {
         this.mc.execute(() -> {
            if (this.dropdownClickGUI == null) {
               this.dropdownClickGUI = new DropdownClickGUI();
            }

            this.mc.method_1507(this.dropdownClickGUI);
         });
      }
   }

   protected void onDisable() {
      if (this.dropdownClickGUI != null) {
         if (this.mc.field_1755 == this.dropdownClickGUI) {
            this.dropdownClickGUI.exit();
         } else {
            this.mc.method_1507((class_437)null);
         }

      }
   }

   @Subscribe
   private void onRenderBloom(RenderBloomEvent event) {
      if (this.mc.field_1755 == this.dropdownClickGUI) {
         drawingBloom = true;
         this.dropdownClickGUI.method_25394(event.getGraphics(), 0, 0, event.getDelta());
         drawingBloom = false;
      }

   }

   public static enum Sorting {
      LENGTH("Length"),
      ALPHABETICAL("Alphabetical");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private Sorting(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static ClickGUIModule.Sorting[] $values() {
         return new ClickGUIModule.Sorting[]{LENGTH, ALPHABETICAL};
      }
   }
}
