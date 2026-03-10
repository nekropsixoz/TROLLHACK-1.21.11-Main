package lol.ethane.mixin.client.entity;

import com.mojang.authlib.GameProfile;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.player.PlayerSlowDownEvent;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.utils.injection.LocalPlayerExtension;
import net.minecraft.class_1268;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_746.class})
public abstract class LocalPlayerMixin extends class_742 implements LocalPlayerExtension {
   @Unique
   private PlayerMovementTickEvent eventMotion;

   public LocalPlayerMixin(class_638 clientLevel, GameProfile gameProfile) {
      super(clientLevel, gameProfile);
   }

   @Shadow
   protected abstract boolean method_3134();

   @Shadow
   protected abstract float method_75410();

   @Inject(
      method = {"method_66282"},
      at = {@At("TAIL")}
   )
   private void applyInput(CallbackInfo ci) {
      if (this.method_3134()) {
         RotationHelper.getClientHandler().tickCamera();
      }

   }

   @Inject(
      method = {"method_3136"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendPositionHead(CallbackInfo ci) {
      this.eventMotion = new PlayerMovementTickEvent(PlayerMovementTickEvent.State.PRE, this.method_24828());
      EventDispatcher.dispatch(this.eventMotion);
      if (this.eventMotion.isCancelled()) {
         ci.cancel();
      }

   }

   @Redirect(
      method = {"method_3136"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_24828()Z"
)
   )
   private boolean hookSendMovementPacketsGround(class_746 instance) {
      return this.eventMotion != null && this.eventMotion.isGround();
   }

   @Inject(
      method = {"method_3136"},
      at = {@At("RETURN")}
   )
   private void sendPositionReturn(CallbackInfo ci) {
      this.eventMotion = new PlayerMovementTickEvent(PlayerMovementTickEvent.State.POST, this.method_24828());
      EventDispatcher.dispatch(this.eventMotion);
   }

   @Redirect(
      method = {"method_67270"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_75410()F"
)
   )
   private float modifyInput(class_746 instance) {
      PlayerSlowDownEvent event = new PlayerSlowDownEvent();
      EventDispatcher.dispatch(event);
      return event.isCancelled() ? 1.0F : this.method_75410();
   }

   public void ethane$swingClient(class_1268 hand) {
      super.method_6104(hand);
   }
}
