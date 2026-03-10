package lol.ethane.feature.module.property.impl.mode;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.feature.module.Module;
import lombok.Generated;
import net.minecraft.class_310;

public abstract class ModuleMode<T extends Module> implements IEventSubscriber {
   protected T module;
   private boolean enabled;
   public final class_310 mc = class_310.method_1551();

   protected ModuleMode(T module) {
      this.module = module;
      EventDispatcher.subscribe(this);
   }

   public void onEnable() {
      if (this.module.getActiveMode() == this) {
         this.enabled = true;
      }

   }

   public void onDisable() {
      if (this.module.getActiveMode() == this) {
         this.enabled = false;
      }

   }

   public boolean isHandlingEvents() {
      return this.enabled;
   }

   public abstract Enum<?> getValue();

   @Generated
   public T getModule() {
      return this.module;
   }
}
