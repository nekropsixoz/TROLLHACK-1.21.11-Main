package lol.ethane.feature.module.property.impl;

import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public final class BooleanProperty extends Property<Boolean> {
   public BooleanProperty(String name, boolean value) {
      super(name);
      this.setValue(value);
   }

   public BooleanProperty(String name, ModuleMode<?> parent, boolean value) {
      super(name, parent);
      this.setValue(value);
   }

   public void toggle() {
      this.setValue(!this.getValue());
   }

   public Boolean getValue() {
      return (Boolean)super.getValue() && !this.isHidden();
   }

   public void applyValue(Object propertyValue) {
      this.setValue(Boolean.parseBoolean(String.valueOf(propertyValue)));
   }
}
