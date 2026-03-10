package lol.ethane.mixin.client;

import lol.ethane.feature.helper.impl.player.timer.TimerHelper;
import net.minecraft.class_9779.class_9781;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_9781.class})
public final class DeltaTrackerMixin {
   @Shadow
   private float field_51958;

   @Inject(
      method = {"method_60639"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_9779$class_9781;field_51962:J",
   opcode = 181,
   ordinal = 0
)}
   )
   private void onAdvanceGameTime(long timeMillis, CallbackInfoReturnable<Integer> cir) {
      float timer = TimerHelper.getInstance().get();
      if (timer > 0.0F) {
         this.field_51958 *= timer;
      }

   }
}
