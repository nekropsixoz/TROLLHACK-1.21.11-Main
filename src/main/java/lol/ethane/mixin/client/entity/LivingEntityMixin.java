package lol.ethane.mixin.client.entity;

import lol.ethane.Ethane;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.player.PlayerJumpEvent;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.module.defined.movement.MovementFixModule;
import net.minecraft.class_1309;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1309.class})
public class LivingEntityMixin {
   @Redirect(
      method = {"method_6043"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_36454()F"
)
   )
   private float redirectYaw(class_1309 instance) {
      if (instance instanceof class_746) {
         return ((MovementFixModule)Ethane.getInstance().getModuleRepository().getModule(MovementFixModule.class)).isEnabled() ? instance.method_36454() : RotationHelper.getClientHandler().getYawOr(instance.method_36454());
      } else {
         return instance.method_36454();
      }
   }

   @Inject(
      method = {"method_6043"},
      at = {@At("HEAD")}
   )
   private void jumpFromGround(CallbackInfo callbackInfo) {
      EventDispatcher.dispatch(new PlayerJumpEvent());
   }
}
