package lol.ethane.feature.module.defined.combat;

import lol.ethane.event.defined.combat.AttackEvent;
import lol.ethane.event.defined.press.MoveInputEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lombok.Generated;

public class SuperKnockbackModule extends Module {
   private final EnumProperty<SuperKnockbackModule.Mode> mode;
   private final NumberProperty wait;
   private final NumberProperty hold;
   private int waitingTicks;
   private int holdingTicks;

   public SuperKnockbackModule() {
      super("Super Knockback", "Increases knockback by manipulating your movement inputs.", ModuleCategory.COMBAT);
      this.mode = new EnumProperty("Mode", SuperKnockbackModule.Mode.WTAP);
      this.wait = new NumberProperty("Wait", 1.0D, 0.0D, 10.0D, 1.0D);
      this.hold = new NumberProperty("Hold", 2.0D, 1.0D, 10.0D, 1.0D);
      this.waitingTicks = -1;
      this.holdingTicks = -1;
      this.addProperties(new Property[]{this.mode, this.wait, this.hold});
   }

   @Subscribe
   private void onAttack(AttackEvent event) {
      if (this.holdingTicks <= 0 && this.waitingTicks <= 0) {
         this.waitingTicks = ((Double)this.wait.getValue()).intValue();
      }

   }

   @Subscribe
   private void onMoveInput(MoveInputEvent event) {
      if (this.waitingTicks > 0) {
         --this.waitingTicks;
         if (this.waitingTicks <= 0) {
            this.holdingTicks = ((Double)this.hold.getValue()).intValue();
         }
      }

      if (this.holdingTicks > 0) {
         switch(((SuperKnockbackModule.Mode)this.mode.getValue()).ordinal()) {
         case 0:
            event.setForward(0.0F);
            break;
         case 1:
            event.setForward(-1.0F);
         }

         --this.holdingTicks;
      }

   }

   private static enum Mode {
      WTAP("W-Tap"),
      STAP("S-Tap");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private Mode(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static SuperKnockbackModule.Mode[] $values() {
         return new SuperKnockbackModule.Mode[]{WTAP, STAP};
      }
   }
}
