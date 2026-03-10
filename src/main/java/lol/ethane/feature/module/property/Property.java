package lol.ethane.feature.module.property;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.property.impl.mode.ModeProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.data.SaveUtil;
import lombok.Generated;

public class Property<T> {
   private final String name;
   @Expose
   @SerializedName("name")
   private String id;
   @Expose
   @SerializedName("value")
   private T value;
   private boolean focused;
   private BooleanSupplier hiddenSupplier;
   private Consumer<T> valueChangeListener;

   protected Property(String name) {
      this.name = name;
      this.id = name;
   }

   protected Property(String name, ModuleMode<?> parent) {
      this.name = name;
      this.id = name;
      Module module = parent.getModule();
      this.hideIf(() -> {
         ModeProperty<?> modeProperty = module.getModeProperty();
         if (modeProperty == null) {
            return false;
         } else {
            return !((Enum)modeProperty.getValue()).equals(parent.getValue());
         }
      });
      module.addProperties(this);
   }

   public final <R extends Property<T>> R hideIf(BooleanSupplier hiddenSupplier) {
      this.hiddenSupplier = hiddenSupplier;
      return (R)this;
   }

   public final String getName() {
      return this.name;
   }

   public final String getId() {
      return this.id;
   }

   public final <R extends Property<T>> R id(String id) {
      this.id = id;
      return (R)this;
   }

   public void setValue(T value) {
      this.value = value;
      SaveUtil.markDirty();
      if (this.valueChangeListener != null) {
         this.valueChangeListener.accept(value);
      }

   }

   public final void onValueChange(Consumer<T> listener) {
      this.valueChangeListener = listener;
   }

   public final boolean isHidden() {
      return this.hiddenSupplier != null && this.hiddenSupplier.getAsBoolean();
   }

   public final boolean isFocused() {
      return this.focused;
   }

   public final void setFocused(boolean focused) {
      this.focused = focused;
   }

   public void applyValue(Object propertyValue) {
   }

   @Generated
   public T getValue() {
      return this.value;
   }
}
