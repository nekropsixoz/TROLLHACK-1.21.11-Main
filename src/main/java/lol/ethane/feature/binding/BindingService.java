package lol.ethane.feature.binding;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ibm.icu.impl.Pair;
import java.util.Optional;
import java.util.Map.Entry;
import lol.ethane.feature.binding.type.InputType;
import lombok.Generated;
import net.minecraft.class_310;

public final class BindingService {
   private final Multimap<Pair<Integer, InputType>, IBindable> bindingMap = HashMultimap.create();

   public void register(int code, IBindable bindable, InputType inputType) {
      this.bindingMap.put(Pair.of(code, inputType), bindable);
   }

   public void clearBindings(IBindable bindable) {
      this.bindingMap.entries().removeIf((entry) -> {
         return entry.getValue() == bindable;
      });
   }

   public void dispatch(int code, InputType inputType) {
      if (class_310.method_1551().field_1755 == null) {
         this.bindingMap.get(Pair.of(code, inputType)).forEach(IBindable::onBindingInteraction);
      }
   }

   public Optional<Pair<Integer, InputType>> getKeyFromBindable(IBindable bindable) {
      return this.bindingMap.entries().stream().filter((entry) -> {
         return ((IBindable)entry.getValue()).equals(bindable);
      }).map(Entry::getKey).findFirst();
   }

   @Generated
   public Multimap<Pair<Integer, InputType>, IBindable> getBindingMap() {
      return this.bindingMap;
   }
}
