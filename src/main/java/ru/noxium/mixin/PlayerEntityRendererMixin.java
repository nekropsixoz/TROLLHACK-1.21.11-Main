package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.noxium.Noxium;
import ru.noxium.module.impl.visuals.NameTags;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(net.minecraft.entity.PlayerLikeEntity player, double squaredDistance,
            CallbackInfoReturnable<Boolean> cir) {
        if (Noxium.get != null && Noxium.get.manager != null) {
            NameTags nameTags = Noxium.get.manager.get(NameTags.class);
            if (nameTags != null && nameTags.enable && nameTags.entityType.get("Player")) {
                cir.setReturnValue(false);
            }
        }
    }
}
