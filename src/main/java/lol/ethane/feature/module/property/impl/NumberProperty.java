package lol.ethane.feature.module.property.impl;

import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MathUtil;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

public class NumberProperty extends Property<Double> {
   private final Double minValue;
   private final Double maxValue;
   private final Double increment;
   private String suffix;

   public NumberProperty(String name, double defaultValue, double minValue, double maxValue, double increment) {
      super(name);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.increment = increment;
      this.setValue(defaultValue);
   }

   public NumberProperty(String name, ModuleMode<?> parent, double defaultValue, double minValue, double maxValue, double increment) {
      super(name, parent);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.increment = increment;
      this.setValue(defaultValue);
   }

   public NumberProperty(String name, String suffix, double defaultValue, double minValue, double maxValue, double increment) {
      this(name, defaultValue, minValue, maxValue, increment);
      this.suffix = suffix;
   }

   public NumberProperty(String name, String suffix, ModuleMode<?> parent, double defaultValue, double minValue, double maxValue, double increment) {
      this(name, parent, defaultValue, minValue, maxValue, increment);
      this.suffix = suffix;
   }

   public void setValue(@NotNull Double value) {
      super.setValue(MathUtil.roundAndClamp(value, this.minValue, this.maxValue, this.increment).doubleValue());
   }

   public void applyValue(Object propertyValue) {
      this.setValue(Double.parseDouble(String.valueOf(propertyValue)));
   }

   @Generated
   public Double getMinValue() {
      return this.minValue;
   }

   @Generated
   public Double getMaxValue() {
      return this.maxValue;
   }

   @Generated
   public Double getIncrement() {
      return this.increment;
   }

   @Generated
   public String getSuffix() {
      return this.suffix;
   }
}
