package ru.noxium.module.impl.visuals;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.Vec3d;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventCameraUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(
   name = "SmoothCamera",
   description = "Makes your camera move smoother",
   category = Category.Visuals,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class SmoothCamera extends Module {
   public static BooleanSetting enableFirstPOV = new BooleanSetting("EnableFirstPOV", false);
   public static BooleanSetting resetOnPerspectiveChange = new BooleanSetting("ResetOnPerspectiveChange", true);
   public static SliderSetting factorH = new SliderSetting("HorizontalFactor", 0.9F, 0F, 1F, 0.01F, false);
   public static SliderSetting factorV = new SliderSetting("VerticalFactor", 0.93F, 0F, 1F, 0.01F, false);

   private Vec3d smoothPos = Vec3d.ZERO;
   private Perspective lastPerspective;

   public SmoothCamera() {
      this.addSettings(new Setting[]{enableFirstPOV, resetOnPerspectiveChange, factorH, factorV});
      this.enable = false; // Disable module by default
   }

   @Override
   public void onDisable() {
      smoothPos = Vec3d.ZERO;
   }

   @EventInit
   public void onCameraUpdate(EventCameraUpdate event) {
      Perspective currentPerspective = mc.options.getPerspective();
      
      if (!this.enable) {
         lastPerspective = currentPerspective;
         return;
      }

      // This provides better responsiveness when switching perspectives
      if (resetOnPerspectiveChange.get() && lastPerspective != currentPerspective) {
         smoothPos = new Vec3d(event.getX(), event.getY(), event.getZ());
         lastPerspective = currentPerspective;
         return;
      }
      
      lastPerspective = currentPerspective;
      
      // Don't smooth for first person since it looks weird
      if (!enableFirstPOV.get() && currentPerspective == Perspective.FIRST_PERSON) {
         smoothPos = new Vec3d(event.getX(), event.getY(), event.getZ());
         return;
      }

      if (isLikelyZero(smoothPos)) {
         smoothPos = new Vec3d(event.getX(), event.getY(), event.getZ());
      }

      double newX = smoothPos.x * factorH.get() + event.getX() * (1 - factorH.get());
      double newY = smoothPos.y * factorV.get() + event.getY() * (1 - factorV.get());
      double newZ = smoothPos.z * factorH.get() + event.getZ() * (1 - factorH.get());
      
      smoothPos = new Vec3d(newX, newY, newZ);
      
      // Apply smoothed position by modifying the event
      event.setX(newX);
      event.setY(newY);
      event.setZ(newZ);
   }

   private boolean isLikelyZero(Vec3d vec) {
      return Math.abs(vec.x) < 0.001 && Math.abs(vec.y) < 0.001 && Math.abs(vec.z) < 0.001;
   }

   public static boolean shouldApplyChanges() {
      SmoothCamera module = (SmoothCamera) ru.noxium.Noxium.get.manager.getModule(SmoothCamera.class);
      return module != null && module.enable;
   }
}
