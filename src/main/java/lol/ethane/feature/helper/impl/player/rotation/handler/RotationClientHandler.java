package lol.ethane.feature.helper.impl.player.rotation.handler;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.mouse.MouseUpdateEvent;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;
import lombok.Generated;
import net.minecraft.class_241;
import net.minecraft.class_310;

public class RotationClientHandler implements IEventSubscriber {
   private boolean disabled;
   private class_241 rotation;
   private boolean ticking;
   private float lastRenderYaw;
   private float renderYaw;
   private float lastRenderPitch;
   private float renderPitch;

   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }

   public RotationClientHandler() {
      EventDispatcher.subscribe(this);
   }

   @Subscribe(
      priority = 1
   )
   public void onMouseUpdate(MouseUpdateEvent event) {
      if (!this.disabled) {
         if (class_310.method_1551().field_1724 != null && !event.isUnlockCursorRun()) {
            if (this.rotation == null) {
               this.initializeRotation();
            }

            double multiplier = event.getSensitivityMultiplier();
            double cursorX = event.getDeltaX() * multiplier;
            double cursorY = event.getDeltaY() * multiplier;
            int yMultiplier = 1;
            if ((Boolean)class_310.method_1551().field_1690.method_42438().method_41753()) {
               yMultiplier = -1;
            }

            float deltaYaw = (float)cursorX * 0.15F;
            float deltaPitch = (float)(cursorY * (double)yMultiplier) * 0.15F;
            float yaw = this.rotation.field_1343 + deltaYaw;
            float pitch = this.rotation.field_1342 + deltaPitch;
            this.rotation = new class_241(yaw, Math.clamp(pitch % 360.0F, -90.0F, 90.0F));
         }

         this.ticking = true;
      }
   }

   public void onPostMouseUpdate() {
      this.ticking = false;
   }

   private void initializeRotation() {
      this.rotation = new class_241(class_310.method_1551().field_1724.method_36454(), class_310.method_1551().field_1724.method_36455());
      this.lastRenderYaw = class_310.method_1551().field_1724.field_3931;
      this.renderYaw = class_310.method_1551().field_1724.field_3932;
      this.lastRenderPitch = class_310.method_1551().field_1724.field_3914;
      this.renderPitch = class_310.method_1551().field_1724.field_3916;
   }

   public void tickCamera() {
      if (!this.disabled) {
         if (this.rotation != null) {
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            this.renderPitch += (this.rotation.field_1342 - this.renderPitch) * 0.5F;
            this.renderYaw += (this.rotation.field_1343 - this.renderYaw) * 0.5F;
         }

      }
   }

   public void onRotationSet() {
      if (!this.ticking) {
         this.rotation = null;
      }

   }

   public float getYawOr(float fallback) {
      return this.rotation == null ? fallback : this.rotation.field_1343;
   }

   public float getPitchOr(float fallback) {
      return this.rotation == null ? fallback : this.rotation.field_1342;
   }

   public float getLastRenderYawOr(float fallback) {
      return this.rotation == null ? fallback : this.lastRenderYaw;
   }

   public float getLastRenderPitchOr(float fallback) {
      return this.rotation == null ? fallback : this.lastRenderPitch;
   }

   public float getRenderYawOr(float fallback) {
      return this.rotation == null ? fallback : this.renderYaw;
   }

   public float getRenderPitchOr(float fallback) {
      return this.rotation == null ? fallback : this.renderPitch;
   }

   @Generated
   public boolean isDisabled() {
      return this.disabled;
   }

   @Generated
   public class_241 getRotation() {
      return this.rotation;
   }

   @Generated
   public boolean isTicking() {
      return this.ticking;
   }

   @Generated
   public float getLastRenderYaw() {
      return this.lastRenderYaw;
   }

   @Generated
   public float getRenderYaw() {
      return this.renderYaw;
   }

   @Generated
   public float getLastRenderPitch() {
      return this.lastRenderPitch;
   }

   @Generated
   public float getRenderPitch() {
      return this.renderPitch;
   }

   @Generated
   public void setRotation(class_241 rotation) {
      this.rotation = rotation;
   }

   @Generated
   public void setTicking(boolean ticking) {
      this.ticking = ticking;
   }

   @Generated
   public void setLastRenderYaw(float lastRenderYaw) {
      this.lastRenderYaw = lastRenderYaw;
   }

   @Generated
   public void setRenderYaw(float renderYaw) {
      this.renderYaw = renderYaw;
   }

   @Generated
   public void setLastRenderPitch(float lastRenderPitch) {
      this.lastRenderPitch = lastRenderPitch;
   }

   @Generated
   public void setRenderPitch(float renderPitch) {
      this.renderPitch = renderPitch;
   }
}
