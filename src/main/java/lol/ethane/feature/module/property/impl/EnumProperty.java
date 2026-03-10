package lol.ethane.feature.module.property.impl;

import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class EnumProperty<T> extends Property<T> {
   private final T[] values;

   @SuppressWarnings("unchecked")
   public EnumProperty(String name, T value) {
      super(name);
      this.setValue(value);
      this.values = (T[])value.getClass().getEnumConstants();
   }

   public EnumProperty(String name, T value, T[] values) {
      super(name);
      this.setValue(value);
      this.values = values;
   }

   @SuppressWarnings("unchecked")
   public EnumProperty(String name, ModuleMode<?> parent, T value) {
      super(name, parent);
      this.setValue(value);
      this.values = (T[])value.getClass().getEnumConstants();
   }

   public void increment() {
      int index = this.getCurrentEnumIndex();
      int nextIndex = (index + 1) % this.values.length;
      this.setValue(this.values[nextIndex]);
   }

   public void decrement() {
      int index = this.getCurrentEnumIndex();
      int prevIndex = (index - 1 + this.values.length) % this.values.length;
      this.setValue(this.values[prevIndex]);
   }

   public T[] enumValues() {
      return this.values;
   }

   public int getCurrentEnumIndex() {
      T currentValue = this.getValue();

      for(int i = 0; i < this.values.length; ++i) {
         if (this.values[i].equals(currentValue)) {
            return i;
         }
      }

      return -1;
   }

   public void applyValue(Object propertyValue) {
      if (propertyValue instanceof String) {
         String strVal = (String)propertyValue;
         Object[] var3 = this.values;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            @SuppressWarnings("unchecked")
            T val = (T)var3[var5];
            String var10000;
            if (val instanceof Enum) {
               Enum<?> e = (Enum)val;
               var10000 = e.name();
            } else {
               var10000 = String.valueOf(val);
            }

            String name = var10000;
            if (name.equalsIgnoreCase(strVal)) {
               this.setValue(val);
               return;
            }
         }
      }

   }
}
