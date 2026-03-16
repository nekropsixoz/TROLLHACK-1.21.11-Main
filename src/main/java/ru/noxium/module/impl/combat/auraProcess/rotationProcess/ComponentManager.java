package ru.noxium.module.impl.combat.auraProcess.rotationProcess;

import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventManager;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.FreeLookUtil;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.ItemUsage;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.RotationProcess;

@Environment(EnvType.CLIENT)
public final class ComponentManager extends HashMap<Class<? extends Component>, Component> {
   public void init() {
      this.add(new FreeLookUtil(), new RotationProcess(), new ItemUsage());
      this.values().forEach(component -> EventManager.register(component));
   }

   public void add(Component... components) {
      for (Component component : components) {
         this.put((Class<? extends Component>)component.getClass(), component);
      }
   }

   public void unregister(Component... components) {
      for (Component component : components) {
         EventManager.unregister(component);
         this.remove(component.getClass());
      }
   }

   public <T extends Component> T get(Class<T> clazz) {
      return this.values().stream().filter(component -> component.getClass() == clazz).map(clazz::cast).findFirst().orElse(null);
   }
}
