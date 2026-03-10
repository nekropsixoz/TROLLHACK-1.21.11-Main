package lol.ethane.feature.drag;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Generated;

public class DraggableRepository {
   private final List<DraggableComponent> draggables = Lists.newArrayList();

   public void register(DraggableComponent... components) {
      this.draggables.addAll(Lists.newArrayList(components));
   }

   public DraggableComponent get(String name) {
      return (DraggableComponent)this.draggables.stream().filter((draggable) -> {
         return draggable.getName().equalsIgnoreCase(name);
      }).findFirst().orElse(null);
   }

   @Generated
   public List<DraggableComponent> getDraggables() {
      return this.draggables;
   }
}
