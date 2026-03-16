package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.noxium.Noxium;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.event.player.EventInput;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.module.impl.combat.AutoTotem;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.util.player.PlayerUtil;

@IModule(
   name = "TargetStrafe",
   description = "Стрейф вокруг цели",
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class TargetStrafe extends Module {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public ModeSetting mode = new ModeSetting("Режим", "Matrix", "Matrix", "Grim");

   public ModeSetting type = new ModeSetting("Точка ходьбы", "Cube", "Cube", "Center", "Circle")
         .hidden(() -> !mode.is("Grim"));

   public ModeSetting typeMatrix = new ModeSetting("Точка для обхода", "Circle", "Cube", "Circle")
         .hidden(() -> !mode.is("Matrix"));

   public SliderSetting grimRadius = new SliderSetting("Радиус обхода", 0.87F, 0.1F, 1.5F, 0.1F, false)
         .hidden(() -> !mode.is("Grim") || (!type.is("Cube") && !type.is("Circle")));

   public MultiBooleanSetting setting = new MultiBooleanSetting(
         "Настройки",
         new BooleanSetting("Auto Jump", true),
         new BooleanSetting("Only Key Pressed", false),
         new BooleanSetting("In front of the target", false),
         new BooleanSetting("Direction Mode", false)
   );

   public ModeSetting directionMode = new ModeSetting("Направление", "Clockwise", "Clockwise", "Counterclockwise", "Random")
         .hidden(() -> !setting.get("Direction Mode"));

   public SliderSetting radius = new SliderSetting("Радиус", 2.5F, 0.1F, 7F, 0.1F, false)
         .hidden(() -> !mode.is("Matrix"));

   public SliderSetting speed = new SliderSetting("Скорость", 0.3F, 0.1F, 1F, 0.1F, false)
         .hidden(() -> !mode.is("Matrix"));

   private int grimPointIndex = 0;

   public TargetStrafe() {
      this.addSettings(new Setting[] {
         mode, type, typeMatrix, grimRadius, radius, speed, setting, directionMode
      });
   }

   public static TargetStrafe getInstance() {
      return Noxium.get.manager.get(TargetStrafe.class);
   }

   private boolean isAutoTotemBlocking() {
      AutoTotem autoTotem = Noxium.get.manager.get(AutoTotem.class);
      if (autoTotem == null) return false;
      if (!autoTotem.enable) return false;
      // Адаптируйте под вашу реализацию AutoTotem
      return false;
   }

   @EventInit
   public void onInput(EventInput event) {
      if (mc.player == null || mc.world == null) return;

      if (isAutoTotemBlocking()) return;

      LivingEntity target = HitAura.target;
      if (target == null || !target.isAlive()) return;

      if (!mode.is("Grim")) return;

      if (setting.get("Only Key Pressed")) {
         if (!mc.options.forwardKey.isPressed() &&
               !mc.options.backKey.isPressed() &&
               !mc.options.leftKey.isPressed() &&
               !mc.options.rightKey.isPressed()) {
            return;
         }
      }

      Vec3d nextPoint = calculateGrimNextPoint(target);
      applyGrimMovement(event, nextPoint);
   }

   private Vec3d calculateGrimNextPoint(LivingEntity target) {
      Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
      double r = grimRadius.get();

      int directionMultiplier = getDirectionMultiplier();

      if (setting.get("In front of the target")) {
         return calculateFrontPoint(target, targetPos, r, directionMultiplier);
      } else {
         return calculateNormalPoint(playerPos, targetPos, r, directionMultiplier);
      }
   }

   private Vec3d calculateFrontPoint(LivingEntity target, Vec3d targetPos, double r, int directionMultiplier) {
      float targetYaw = target.getYaw();

      if (type.is("Center")) {
         return targetPos.add(
               -Math.sin(Math.toRadians(targetYaw)) * r * directionMultiplier,
               0,
               Math.cos(Math.toRadians(targetYaw)) * r * directionMultiplier);
      } else {
         double offset = Math.cos(System.currentTimeMillis() / 500.0) * r * directionMultiplier;
         return targetPos.add(
               -Math.sin(Math.toRadians(targetYaw)) * r + Math.cos(Math.toRadians(targetYaw)) * offset,
               0,
               Math.cos(Math.toRadians(targetYaw)) * r + Math.sin(Math.toRadians(targetYaw)) * offset
         );
      }
   }

   private Vec3d calculateNormalPoint(Vec3d playerPos, Vec3d targetPos, double r, int directionMultiplier) {
      if (type.is("Cube")) {
         Vec3d[] points = new Vec3d[]{
               new Vec3d(targetPos.x - r, playerPos.y, targetPos.z - r),
               new Vec3d(targetPos.x - r, playerPos.y, targetPos.z + r),
               new Vec3d(targetPos.x + r, playerPos.y, targetPos.z + r),
               new Vec3d(targetPos.x + r, playerPos.y, targetPos.z - r)
         };

         if (playerPos.distanceTo(points[grimPointIndex]) < 0.5) {
            grimPointIndex = (grimPointIndex + directionMultiplier + points.length) % points.length;
         }

         return points[grimPointIndex];
      } else if (type.is("Circle")) {
         double baseAngle = (System.currentTimeMillis() % 3600L) / 3600.0 * 4 * Math.PI;
         double angle = directionMultiplier > 0 ? baseAngle : (2 * Math.PI - baseAngle);

         return new Vec3d(
               targetPos.x + Math.cos(angle) * r,
               playerPos.y,
               targetPos.z + Math.sin(angle) * r
         );
      } else {
         return new Vec3d(targetPos.x, playerPos.y, targetPos.z);
      }
   }

   private void applyGrimMovement(EventInput event, Vec3d nextPoint) {
      Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      Vec3d direction = nextPoint.subtract(playerPos).normalize();

      float yaw = mc.player.getYaw();
      float movementAngle = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90F;
      float angleDiff = MathHelper.wrapDegrees(movementAngle - yaw);

      float forward = 0, strafe = 0;

      if (angleDiff >= -22.5 && angleDiff < 22.5) {
         forward = 1;
      } else if (angleDiff >= 22.5 && angleDiff < 67.5) {
         forward = 1; strafe = 1;
      } else if (angleDiff >= 67.5 && angleDiff < 112.5) {
         strafe = 1;
      } else if (angleDiff >= 112.5 && angleDiff < 157.5) {
         forward = -1; strafe = 1;
      } else if (angleDiff >= -67.5 && angleDiff < -22.5) {
         forward = 1; strafe = -1;
      } else if (angleDiff >= -112.5 && angleDiff < -67.5) {
         strafe = -1;
      } else if (angleDiff >= -157.5 && angleDiff < -112.5) {
         forward = -1; strafe = -1;
      } else {
         forward = -1;
      }

      event.setForward(forward);
      event.setStrafe(strafe);

      if (setting.get("Auto Jump") && mc.player.isOnGround()) {
         event.setJump(true);
      }
   }

   @EventInit
   public void onTick(EventUpdate event) {
      if (mc.player == null || mc.world == null) return;

      if (isAutoTotemBlocking()) return;

      LivingEntity target = HitAura.target;
      if (target == null || !target.isAlive()) return;

      if (!mode.is("Matrix")) return;

      if (setting.get("Only Key Pressed")) {
         if (!mc.options.forwardKey.isPressed() &&
               !mc.options.backKey.isPressed() &&
               !mc.options.leftKey.isPressed() &&
               !mc.options.rightKey.isPressed()) {
            return;
         }
      }

      if (setting.get("Auto Jump") && mc.player.isOnGround()) {
         mc.player.jump();
      }

      processMatrixStrafe(target);
   }

   private int getDirectionMultiplier() {
      int directionMultiplier = 1;
      if (setting.get("Direction Mode")) {
         if (directionMode.is("Counterclockwise")) {
            directionMultiplier = -1;
         } else if (directionMode.is("Random")) {
            long time = System.currentTimeMillis() / 3000;
            directionMultiplier = (time % 2 == 0) ? 1 : -1;
         }
      }
      return directionMultiplier;
   }

   private void processMatrixStrafe(LivingEntity target) {
      Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
      double r = radius.get();

      int directionMultiplier = getDirectionMultiplier();

      if (setting.get("In front of the target")) {
         processMatrixFrontStrafe(target, playerPos, targetPos, r, directionMultiplier);
         return;
      }

      if (typeMatrix.is("Cube")) {
         processMatrixCubeStrafe(playerPos, targetPos, r, directionMultiplier);
      } else if (typeMatrix.is("Circle")) {
         processMatrixCircleStrafe(playerPos, targetPos, r, directionMultiplier);
      }
   }

   private void processMatrixFrontStrafe(LivingEntity target, Vec3d playerPos, Vec3d targetPos, double r, int directionMultiplier) {
      float targetYaw = target.getYaw();
      double x = targetPos.x - Math.sin(Math.toRadians(targetYaw)) * r * directionMultiplier;
      double z = targetPos.z + Math.cos(Math.toRadians(targetYaw)) * r * directionMultiplier;

      float yaw = (float) Math.toDegrees(Math.atan2(z - playerPos.z, x - playerPos.x)) - 90F;
      double motionSpeed = speed.get();
      mc.player.setVelocity(-Math.sin(Math.toRadians(yaw)) * motionSpeed,
            mc.player.getVelocity().y,
            Math.cos(Math.toRadians(yaw)) * motionSpeed);
   }

   private void processMatrixCubeStrafe(Vec3d playerPos, Vec3d targetPos, double r, int directionMultiplier) {
      Vec3d[] points = new Vec3d[]{
            new Vec3d(targetPos.x - r, playerPos.y, targetPos.z - r),
            new Vec3d(targetPos.x - r, playerPos.y, targetPos.z + r),
            new Vec3d(targetPos.x + r, playerPos.y, targetPos.z + r),
            new Vec3d(targetPos.x + r, playerPos.y, targetPos.z - r)
      };

      if (playerPos.distanceTo(points[grimPointIndex]) < 0.5) {
         grimPointIndex = (grimPointIndex + directionMultiplier + points.length) % points.length;
      }

      Vec3d nextPoint = points[grimPointIndex];
      Vec3d dirVec = nextPoint.subtract(playerPos).normalize();

      float yaw = (float) Math.toDegrees(Math.atan2(dirVec.z, dirVec.x)) - 90F;
      double motionSpeed = speed.get();

      mc.player.setVelocity(-Math.sin(Math.toRadians(yaw)) * motionSpeed,
            mc.player.getVelocity().y,
            Math.cos(Math.toRadians(yaw)) * motionSpeed);
   }

   private void processMatrixCircleStrafe(Vec3d playerPos, Vec3d targetPos, double r, int directionMultiplier) {
      double angle = Math.atan2(playerPos.z - targetPos.z, playerPos.x - targetPos.x);
      angle += directionMultiplier * speed.get() / Math.max(playerPos.distanceTo(targetPos), r);

      double x = targetPos.x + r * Math.cos(angle);
      double z = targetPos.z + r * Math.sin(angle);

      float yaw = (float) Math.toDegrees(Math.atan2(z - playerPos.z, x - playerPos.x)) - 90F;
      double motionSpeed = speed.get();

      mc.player.setVelocity(-Math.sin(Math.toRadians(yaw)) * motionSpeed,
            mc.player.getVelocity().y,
            Math.cos(Math.toRadians(yaw)) * motionSpeed);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      grimPointIndex = 0;
   }
}
