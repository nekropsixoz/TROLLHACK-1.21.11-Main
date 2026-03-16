package ru.noxium.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.noxium.module.impl.visuals.CustomWorld;

@Environment(EnvType.CLIENT)
@Mixin({Properties.class})
public abstract class ClientWorldPropertiesMixin {
   @ModifyReturnValue(
      method = {"getTimeOfDay"},
      at = {@At("RETURN")}
   )
   private long hookGetTime(long original) {
      return CustomWorld.customTime;
   }
}
