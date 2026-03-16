package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.event.EventManager;
import ru.noxium.event.player.EventRotation;
import ru.noxium.event.impl.EventCameraUpdate;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin {

    @Unique
    private EventRotation noxium$rotationEvent;

    @Unique
    private float noxium$originalYaw;

    @Unique
    private float noxium$originalPitch;

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    /*
     * =========================
     *  CAMERA POSITION FIX
     * =========================
     */

    @Redirect(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"
        )
    )
    private void redirectSetPos(Camera instance, double x, double y, double z) {

        // Create event with the REAL freshly calculated position
        EventCameraUpdate event = new EventCameraUpdate(x, y, z);
        EventManager.call(event);

        // Apply modified coordinates (SmoothCamera edits them here)
        this.setPos(
            event.getX(),
            event.getY(),
            event.getZ()
        );
    }

    /*
     * =========================
     *  ROTATION EVENT
     * =========================
     */

    @Inject(
        method = "update",
        at = @At("HEAD")
    )
    private void onUpdateHead(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {

        if (focusedEntity != null) {
            this.noxium$originalYaw = focusedEntity.getYaw(tickDelta);
            this.noxium$originalPitch = focusedEntity.getPitch(tickDelta);

            this.noxium$rotationEvent =
                new EventRotation(this.noxium$originalYaw, this.noxium$originalPitch, tickDelta);

            EventManager.call(this.noxium$rotationEvent);
        } else {
            this.noxium$rotationEvent = null;
        }
    }

    @Redirect(
        method = "update",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"
        )
    )
    private void redirectSetRotation(Camera instance, float yaw, float pitch) {

        if (this.noxium$rotationEvent == null) {
            this.setRotation(yaw, pitch);
            return;
        }

        this.setRotation(
            this.noxium$rotationEvent.getYaw(),
            this.noxium$rotationEvent.getPitch()
        );
    }

    @Inject(
        method = "update",
        at = @At("RETURN")
    )
    private void onUpdateReturn(CallbackInfo ci) {
        this.noxium$rotationEvent = null;
    }
}