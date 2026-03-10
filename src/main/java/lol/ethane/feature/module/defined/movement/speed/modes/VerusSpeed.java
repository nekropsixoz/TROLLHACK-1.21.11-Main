package lol.ethane.feature.module.defined.movement.speed.modes;

import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.speed.SpeedModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.utils.math.MovementUtil;

public class VerusSpeed extends ModuleMode<SpeedModule> {
   private final Property<Double> damageMultiplier = new NumberProperty("Damage multiplier", this, 1.5D, 1.0D, 4.0D, 0.01D);

   public VerusSpeed(SpeedModule module) {
      super(module);
   }

   @Subscribe
   private void onTick(PlayerMovementTickEvent event) {
      if (MovementUtil.isMoving()) {
         MovementUtil.strafe(this.mc.field_1724.field_6235 > 0 ? 0.36D * (Double)this.damageMultiplier.getValue() : 0.36D);
         if (this.mc.field_1724.method_24828()) {
            this.mc.field_1724.method_6043();
            MovementUtil.strafe(ThreadLocalRandom.current().nextDouble(0.36D, 0.6D));
         }
      }

   }

   public Enum<?> getValue() {
      return SpeedModule.Mode.VERUS;
   }
}
