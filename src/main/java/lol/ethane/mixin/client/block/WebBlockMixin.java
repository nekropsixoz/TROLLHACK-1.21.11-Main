package lol.ethane.mixin.client.block;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.game.WebBlockCollisionEvent;
import net.minecraft.class_10774;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2560;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2560.class})
public class WebBlockMixin {
   @Inject(
      method = {"method_9548"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void entityInside(class_2680 blockState, class_1937 level, class_2338 blockPos, class_1297 entity, class_10774 insideBlockEffectApplier, boolean bl, CallbackInfo ci) {
      WebBlockCollisionEvent event = new WebBlockCollisionEvent(blockPos);
      EventDispatcher.dispatch(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}
