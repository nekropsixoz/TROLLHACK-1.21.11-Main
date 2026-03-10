package lol.ethane.feature.module.defined.combat.velocity.modes;

import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.combat.velocity.VelocityModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class JumpResetVelocity extends ModuleMode<VelocityModule> {
   private final Property<Boolean> polar = new BooleanProperty("Polar", this, true);
   private boolean hitProcessed;
   private int jumpTicks = -1;

   public JumpResetVelocity(VelocityModule module) {
      super(module);
   }

   @Subscribe
   private void onMoveInput(MoveInputEvent event) {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1724.field_6235 > 0) {
            if (!this.hitProcessed) {
               if ((Boolean)this.polar.getValue()) {
                  if (ThreadLocalRandom.current().nextDouble() <= 0.75D) {
                     this.jumpTicks = ThreadLocalRandom.current().nextInt(0, 5);
                  }
               } else {
                  this.jumpTicks = 0;
               }

               this.hitProcessed = true;
            }
         } else {
            this.hitProcessed = false;
         }

         if (this.jumpTicks >= 0) {
            if (this.jumpTicks == 0) {
               event.setJump(true);
               this.jumpTicks = -1;
            } else {
               --this.jumpTicks;
            }
         }

      }
   }

   public Enum<?> getValue() {
      return VelocityModule.Mode.JUMP_$RESET;
   }
}
