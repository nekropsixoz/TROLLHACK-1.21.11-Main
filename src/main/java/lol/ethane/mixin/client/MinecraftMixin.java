package lol.ethane.mixin.client;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.game.GameSwitchWorldEvent;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.protection.MinecraftMixinProtection;
import net.minecraft.class_310;
import net.minecraft.class_542;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_310.class})
public abstract class MinecraftMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void postInit(class_542 gameConfig, CallbackInfo ci) {
      MinecraftMixinProtection.init();
   }

   @Inject(
      method = {"method_1490"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;close()V",
   shift = Shift.AFTER
)}
   )
   private void destroyAfterClose(CallbackInfo ci) {
      MinecraftMixinProtection.shutdown();
   }

   @Inject(
      method = {"method_1574"},
      at = {@At("HEAD")}
   )
   private void tickHead(CallbackInfo ci) {
      EventDispatcher.dispatch(new PreGameTickEvent());
   }

   @Inject(
      method = {"method_1481"},
      at = {@At("HEAD")}
   )
   private void setLevel(class_638 clientLevel, CallbackInfo ci) {
      EventDispatcher.dispatch(new GameSwitchWorldEvent());
   }
}
