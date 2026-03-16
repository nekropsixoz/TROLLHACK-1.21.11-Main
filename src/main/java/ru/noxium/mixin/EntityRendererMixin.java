package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.Noxium;
import ru.noxium.module.impl.visuals.NameTags;
import ru.noxium.util.render.capture.EntityFramebufferCaptureManager;

@Environment(EnvType.CLIENT)
@Mixin({ EntityRenderer.class })
public abstract class EntityRendererMixin<S extends EntityRenderState> {
   @Inject(method = { "renderLabelIfPresent" }, at = { @At("HEAD") }, cancellable = true)
   private void renderLabelIfPresent(S state, MatrixStack matrices, OrderedRenderCommandQueue queue,
         CameraRenderState cameraRenderState, CallbackInfo ci) {
      if (Noxium.get != null && Noxium.get.manager != null) {
         NameTags nameTags = Noxium.get.manager.get(NameTags.class);
         if (nameTags != null && nameTags.enable && this.canRemove(state, nameTags)) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
   private void onHasLabel(net.minecraft.entity.Entity entity, double squaredDistance,
         org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
      if (Noxium.get != null && Noxium.get.manager != null) {
         NameTags nameTags = Noxium.get.manager.get(NameTags.class);
         if (nameTags != null && nameTags.enable && this.canRemove(entity, nameTags)) {
            cir.setReturnValue(false);
         }
      }
   }

   @Unique
   private boolean canRemove(net.minecraft.entity.Entity entity, NameTags esp) {
      if (entity instanceof net.minecraft.entity.player.PlayerEntity) {
         return esp.entityType.get("Player");
      }
      if (entity instanceof net.minecraft.entity.LivingEntity) {
         return esp.entityType.get("Mobs");
      }
      if (entity instanceof net.minecraft.entity.ItemEntity) {
         return esp.entityType.get("Item");
      }
      return false;
   }

   @Unique
   private boolean canRemove(EntityRenderState state, NameTags esp) {
      if (state instanceof PlayerEntityRenderState) {
         return esp.entityType.get("Player");
      }
      if (state instanceof LivingEntityRenderState) {
         return esp.entityType.get("Mobs");
      }
      if (state instanceof ItemEntityRenderState) {
         return esp.entityType.get("Item");
      }
      return false;
   }

   @Inject(method = { "renderLabelIfPresent" }, at = { @At("HEAD") }, cancellable = true)
   private void skipLabelDuringCapture(S state, MatrixStack matrices,
         OrderedRenderCommandQueue queue,
         CameraRenderState cameraRenderState, CallbackInfo ci) {
      if (EntityFramebufferCaptureManager.getInstance().isExecutingCapturePass()) {
         ci.cancel();
      }
   }
}
