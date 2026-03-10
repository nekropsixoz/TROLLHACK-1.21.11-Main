package lol.ethane.feature.module.property.impl.mode;

import java.util.Iterator;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.property.Property;
import lombok.Generated;

public class ModeProperty<T extends Enum<T>> extends Property<T> {
   private final T[] values;
   private Module module;

   public ModeProperty(String name, T value) {
      super(name);
      this.setValue(value);
      this.values = this.getEnumConstants();
   }

   public ModeProperty(String name, T value, T[] values) {
      super(name);
      this.setValue(value);
      this.values = values;
   }

   public ModeProperty(String name, Module module, T value, T[] values) {
      super(name);
      this.setValue(value);
      this.values = values;
      this.module = module;
      module.setModeProperty(this);
   }

   public ModeProperty(String name, ModuleMode<?> parent, T value) {
      super(name, parent);
      this.setValue(value);
      this.values = this.getEnumConstants();
   }

   public ModeProperty(String name, Module module, T value) {
      super(name);
      this.setValue(value);
      this.values = this.getEnumConstants();
      this.module = module;
      module.setModeProperty(this);
   }

   @SuppressWarnings("unchecked")
   private T[] getEnumConstants() {
      return (T[])((Enum)this.getValue()).getClass().getEnumConstants();
   }

   public void setModule(Module module) {
      this.module = module;
   }

   public void setValueOrdinal(int value) {
      if (this.module != null && this.module.isEnabled()) {
         this.module.getModuleModeList().forEach(ModuleMode::onDisable);
      }

      this.setValue(this.values[value]);
      if (this.module != null) {
         Iterator var2 = this.module.getModuleModeList().iterator();

         while(var2.hasNext()) {
            ModuleMode<?> mode = (ModuleMode)var2.next();
            if (mode.getValue().ordinal() == value && this.module.isEnabled()) {
               mode.onEnable();
               break;
            }
         }
      }

   }

   public void cycle(boolean forwards) {
      int currentIndex = ((Enum)this.getValue()).ordinal();
      int nextIndex = (currentIndex + (forwards ? 1 : this.values.length - 1)) % this.values.length;
      this.setValueOrdinal(nextIndex);
   }

   public boolean is(T value) {
      return this.getValue() == value;
   }

   public void applyValue(Object propertyValue) {
      if (propertyValue instanceof String) {
         String valueString = (String)propertyValue;
         Enum[] var3 = this.values;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            @SuppressWarnings("unchecked")
            T possibleValue = (T)var3[var5];
            if (possibleValue.name().equals(valueString)) {
               this.setValueOrdinal(possibleValue.ordinal());
               break;
            }
         }
      }

   }

   @Generated
   public T[] getValues() {
      return this.values;
   }
}
