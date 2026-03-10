package lol.ethane.mixin.client.multiplayer;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.combat.AttackEvent;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_636.class})
public class MultiPlayerGameModeMixin {
   @Inject(
      method = {"method_2918"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_636;method_2911()V",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void onAttack(class_1657 player, class_1297 target, CallbackInfo ci) {
      AttackEvent event = new AttackEvent(target);
      EventDispatcher.dispatch(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}
