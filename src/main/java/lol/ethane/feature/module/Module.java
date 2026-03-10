package lol.ethane.feature.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.feature.binding.IBindable;
import lol.ethane.feature.module.defined.render.ArrayListModule;
import lol.ethane.feature.module.property.IPropertyListProvider;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.data.SaveUtil;
import lombok.Generated;
import net.minecraft.class_310;

public class Module implements IBindable, IPropertyListProvider, IEventSubscriber {
   public final class_310 mc = class_310.method_1551();
   private final String name;
   private final String description;
   private final ModuleCategory category;
   @Expose
   @SerializedName("name")
   private final String id;
   @Expose
   @SerializedName("enabled")
   private boolean enabled;
   @Expose
   @SerializedName("visible")
   private boolean visible = true;
   @Expose
   @SerializedName("properties")
   private final List<Property<?>> propertyList = new ArrayList();
   private final List<ModuleMode<?>> moduleModeList = new ArrayList();
   private ModeProperty<?> modeProperty;

   protected Module(String name, String description, ModuleCategory category) {
      this.name = name;
      this.description = description;
      this.category = category;
      this.id = name.toLowerCase().replace(" ", "");
      EventDispatcher.subscribe(this);
   }

   public final void setEnabled(boolean enabled) {
      if (this.enabled != enabled) {
         this.enabled = enabled;
         SaveUtil.markDirty();
         if (ArrayListModule.INSTANCE != null) {
            ArrayListModule.INSTANCE.setNeedsSort();
         }

         if (enabled) {
            this.onEnable();
         } else {
            this.onDisable();
         }

      }
   }

   public final void addProperties(Property<?>... properties) {
      Property[] var2 = properties;
      int var3 = properties.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Property<?> property = var2[var4];
         if (property != null) {
            this.propertyList.add(property);
            property.onValueChange((v) -> {
               if (ArrayListModule.INSTANCE != null) {
                  ArrayListModule.INSTANCE.setNeedsSort();
               }

            });
         }
      }

   }

   protected void onEnable() {
      if (this.getActiveMode() != null) {
         this.getActiveMode().onEnable();
      }

   }

   protected void onDisable() {
      if (this.getActiveMode() != null) {
         this.getActiveMode().onDisable();
      }

   }

   @SafeVarargs
   public final <T extends Module> void addModes(ModeProperty<?> target, ModuleMode<T>... modes) {
      this.modeProperty = target;
      target.setModule(this);
      Collections.addAll(this.moduleModeList, modes);
   }

   public final ModuleMode<?> getActiveMode() {
      if (this.modeProperty == null) {
         return null;
      } else {
         Iterator var1 = this.moduleModeList.iterator();

         ModuleMode mode;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            mode = (ModuleMode)var1.next();
         } while(!mode.getValue().equals(this.modeProperty.getValue()));

         return mode;
      }
   }

   public String getSuffix() {
      return null;
   }

   public void onBindingInteraction() {
      this.setEnabled(!this.isEnabled());
   }

   public boolean isHandlingEvents() {
      return this.enabled;
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   @Generated
   public class_310 getMc() {
      return this.mc;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public ModuleCategory getCategory() {
      return this.category;
   }

   @Generated
   public String getId() {
      return this.id;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   public boolean isVisible() {
      return this.visible;
   }

   @Generated
   public List<Property<?>> getPropertyList() {
      return this.propertyList;
   }

   @Generated
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Generated
   public List<ModuleMode<?>> getModuleModeList() {
      return this.moduleModeList;
   }

   @Generated
   public void setModeProperty(ModeProperty<?> modeProperty) {
      this.modeProperty = modeProperty;
   }

   @Generated
   public ModeProperty<?> getModeProperty() {
      return this.modeProperty;
   }
}
