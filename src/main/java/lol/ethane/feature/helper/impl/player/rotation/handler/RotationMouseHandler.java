package lol.ethane.feature.helper.impl.player.rotation.handler;

import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.defined.mouse.MouseUpdateEvent;
import lol.ethane.event.subscriber.IEventSubscriber;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import lol.ethane.utils.rotation.RotationUtil;
import lombok.Generated;
import net.minecraft.class_241;
import net.minecraft.class_310;
import net.minecraft.class_3532;

public final class RotationMouseHandler implements IEventSubscriber {
   private IRotationModel rotationModel;
   private class_241 targetRotation;
   private class_241 tickRotation;
   private boolean active;
   private boolean forward;
   private boolean ticked;
   private boolean unlockCursor;

   public RotationMouseHandler() {
      EventDispatcher.subscribe(this);
   }

   @Subscribe
   public void onMouseUpdate(MouseUpdateEvent event) {
      if (this.tickRotation != null && this.targetRotation != null && class_310.method_1551().field_1724 != null && this.active) {
         if (!this.forward) {
            RotationClientHandler clientHandler = RotationHelper.getClientHandler();
            this.targetRotation = clientHandler.getRotation();
            if (this.targetRotation == null) {
               this.ticked = false;
               return;
            }
         }

         float tickDelta;
         if (this.ticked) {
            tickDelta = 1.0F;
            this.ticked = false;
         } else {
            tickDelta = class_310.method_1551().method_61966().method_60637(false);
         }

         double sensitivityMultiplier = event.getSensitivityMultiplier();
         class_241 tickedRotation = this.rotationModel.tick(this.tickRotation, this.targetRotation, tickDelta);
         double deltaYaw = (double)class_3532.method_15393(tickedRotation.field_1343 - class_310.method_1551().field_1724.method_36454());
         double cursorDeltaX = (double)Math.round(RotationUtil.getCursorDelta(deltaYaw, sensitivityMultiplier));
         double deltaPitch = (double)(tickedRotation.field_1342 - class_310.method_1551().field_1724.method_36455());
         double cursorDeltaY = (double)Math.round(RotationUtil.getCursorDelta(deltaPitch, sensitivityMultiplier));
         if ((Boolean)class_310.method_1551().field_1690.method_42438().method_41753()) {
            cursorDeltaY *= -1.0D;
         }

         event.setDeltaX(cursorDeltaX);
         event.setDeltaY(cursorDeltaY);
         event.setHandled(true);
         if (!this.forward && (double)RotationUtil.getRotationDifference(tickedRotation, this.targetRotation) == 0.0D) {
            this.rotationModel = null;
            this.targetRotation = null;
            this.active = false;
         }

      } else {
         this.ticked = false;
      }
   }

   @Subscribe(
      priority = 8
   )
   private void onTick(PreGameTickEvent event) {
      if (class_310.method_1551().field_1724 != null) {
         if (this.active) {
            this.ticked = true;
            class_310.method_1551().field_1729.method_55793();
            this.ticked = false;
         }

         if (this.forward) {
            RotationClientHandler clientHandler = RotationHelper.getClientHandler();
            this.targetRotation = clientHandler.getRotation();
            this.forward = false;
         }

         this.tickRotation = new class_241(class_310.method_1551().field_1724.method_36454(), class_310.method_1551().field_1724.method_36455());
         this.unlockCursor = false;
      }
   }

   public boolean isUnlockCursor() {
      return this.unlockCursor && this.ticked;
   }

   public void rotate(class_241 targetRotation, IRotationModel rotationModel) {
      this.targetRotation = targetRotation;
      this.rotationModel = rotationModel;
      this.forward = true;
      this.active = true;
      this.ticked = true;
      class_310.method_1551().field_1729.method_55793();
      this.ticked = false;
   }

   @Generated
   public IRotationModel getRotationModel() {
      return this.rotationModel;
   }

   @Generated
   public class_241 getTargetRotation() {
      return this.targetRotation;
   }

   @Generated
   public class_241 getTickRotation() {
      return this.tickRotation;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }

   @Generated
   public boolean isForward() {
      return this.forward;
   }

   @Generated
   public boolean isTicked() {
      return this.ticked;
   }

   @Generated
   public void setRotationModel(IRotationModel rotationModel) {
      this.rotationModel = rotationModel;
   }

   @Generated
   public void setTargetRotation(class_241 targetRotation) {
      this.targetRotation = targetRotation;
   }

   @Generated
   public void setTickRotation(class_241 tickRotation) {
      this.tickRotation = tickRotation;
   }

   @Generated
   public void setActive(boolean active) {
      this.active = active;
   }

   @Generated
   public void setForward(boolean forward) {
      this.forward = forward;
   }

   @Generated
   public void setTicked(boolean ticked) {
      this.ticked = ticked;
   }

   @Generated
   public void setUnlockCursor(boolean unlockCursor) {
      this.unlockCursor = unlockCursor;
   }
}
