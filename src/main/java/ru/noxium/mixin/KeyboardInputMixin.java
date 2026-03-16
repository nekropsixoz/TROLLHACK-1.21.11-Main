package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.event.EventManager;
import ru.noxium.event.player.EventInput;

@Environment(EnvType.CLIENT)
@Mixin({ KeyboardInput.class })
public abstract class KeyboardInputMixin {
   @Unique
   private EventInput noxium$inputEvent;

   @Inject(method = { "tick" }, at = {
         @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;<init>(FF)V", shift = Shift.BEFORE) })
   private void onTickBeforeMovementVector(CallbackInfo ci) {
      KeyboardInput self = (KeyboardInput) (Object) this;
      float f = getMovementMultiplier(self.playerInput.forward(), self.playerInput.backward());
      float g = getMovementMultiplier(self.playerInput.left(), self.playerInput.right());
      this.noxium$inputEvent = new EventInput(f, g, self.playerInput.jump(), self.playerInput.sneak(), 0.3);
      EventManager.call(this.noxium$inputEvent);
   }

   @Redirect(method = { "tick" }, at = @At(value = "NEW", target = "Lnet/minecraft/util/math/Vec2f;"))
   private Vec2f redirectVec2fCreation(float x, float y) {
      return this.noxium$inputEvent != null
            ? new Vec2f(this.noxium$inputEvent.getStrafe(), this.noxium$inputEvent.getForward()).normalize()
            : new Vec2f(x, y).normalize();
   }

   @Inject(method = { "tick" }, at = {
         @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;playerInput:Lnet/minecraft/util/PlayerInput;", opcode = 181, shift = Shift.AFTER) })
   private void onTickAfterPlayerInput(CallbackInfo ci) {
      if (this.noxium$inputEvent != null) {
         KeyboardInput self = (KeyboardInput) (Object) this;
         PlayerInput current = self.playerInput;
         PlayerInput modifiedInput = new PlayerInput(
               current.forward(),
               current.backward(),
               current.left(),
               current.right(),
               this.noxium$inputEvent.isJump(),
               this.noxium$inputEvent.isSneak(),
               current.sprint());
         self.playerInput = modifiedInput;
      }
   }

   @Inject(method = { "tick" }, at = { @At("RETURN") })
   private void onTickReturn(CallbackInfo ci) {
      this.noxium$inputEvent = null;
   }

   @Unique
   private static float getMovementMultiplier(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }
}
