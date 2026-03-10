package lol.ethane.click.framework;

import lol.ethane.feature.module.property.Property;
import lombok.Generated;

public class PropertyComponent<T extends Property<?>> extends Component {
   private final T value;
   public Component parent;

   @Generated
   public T getValue() {
      return this.value;
   }

   @Generated
   public Component getParent() {
      return this.parent;
   }

   @Generated
   public PropertyComponent(T value) {
      this.value = value;
   }
}
