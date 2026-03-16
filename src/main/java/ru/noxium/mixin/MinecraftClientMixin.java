package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.noxium.Noxium;
import ru.noxium.event.EventManager;
import ru.noxium.event.impl.EventChangeWorld;
import ru.noxium.event.lifecycle.ClientTickEvent;

@Environment(EnvType.CLIENT)
@Mixin({ MinecraftClient.class })
public abstract class MinecraftClientMixin {
   @Inject(method = { "tick" }, at = { @At("HEAD") })
   private void initRenderer(CallbackInfo ci) {
      if (Noxium.isModInitialized()) {
         Noxium.ensureRendererInitialized();
      }
   }

   @Inject(method = { "tick" }, at = { @At("TAIL") })
   private void publishClientTick(CallbackInfo ci) {
      if (Noxium.isModInitialized()) {
         MinecraftClient client = (MinecraftClient) (Object) this;
         if (!client.isPaused()) {
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;
            if (player != null && world != null) {
               EventManager.call(new ClientTickEvent(client));
            }
         }
      }
   }

   @Inject(method = { "joinWorld" }, at = { @At("TAIL") })
   public void loadWorld(CallbackInfo ci) {
      EventManager.call(new EventChangeWorld());
   }

   @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
   private void getWindowTitle(CallbackInfoReturnable<String> info) {
      info.setReturnValue("Noxium Client Build : 1.0.1");
   }
}
