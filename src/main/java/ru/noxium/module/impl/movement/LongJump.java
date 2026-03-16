package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(
   name = "Long Jump",
   description = "",
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class LongJump extends Module {
   public static ModeSetting mode = new ModeSetting("Режим", "Air", "Air", "Water");
   public static SliderSetting jumps = new SliderSetting("Прыжки", 1.0F, 1.0F, 5.0F, 1.0F, false);
   public static SliderSetting power = new SliderSetting("Сила", 0.42F, 0.1F, 1.0F, 0.01F, false);
   public static SliderSetting waterAdd = new SliderSetting("WaterAdd", 0.018F, 0.0F, 0.08F, 0.001F, false).hidden(() -> !mode.is("Water"));
   public static SliderSetting waterMaxY = new SliderSetting("WaterMaxY", 0.33F, 0.05F, 0.6F, 0.01F, false).hidden(() -> !mode.is("Water"));
   private int jumpsUsed;

   public LongJump() {
      this.addSettings(new Setting[]{mode, jumps, power, waterAdd, waterMaxY});
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (mc.player != null && mc.world != null) {
         if (mode.is("Water")) {
            this.handleWater();
         } else {
            if (mc.player.isOnGround()) {
               this.jumpsUsed = 0;
            }

            if (!mc.player.isTouchingWater() && !mc.player.isInLava() && !mc.player.isFlyingVehicle()) {
               if (!mc.player.getAbilities().flying) {
                  if (!mc.player.hasVehicle()) {
                     while (mc.options.jumpKey.isPressed()) {
                        if (mc.player.isOnGround()) {
                           return;
                        }

                        if (this.jumpsUsed >= jumps.get()) {
                           return;
                        }

                        mc.player.setVelocity(mc.player.getVelocity().x, power.get(), mc.player.getVelocity().z);
                        mc.player.velocityDirty = true;
                        this.jumpsUsed++;
                     }
                  }
               }
            }
         }
      }
   }

   private void handleWater() {
      if (mc.player.isTouchingWater() || this.isPlayerInWaterBlock()) {
         if (mc.options.jumpKey.isPressed()) {
            double currentY = mc.player.getVelocity().y;
            double targetY = Math.min(currentY + waterAdd.get(), (double)waterMaxY.get());
            if (targetY > currentY) {
               mc.player.setVelocity(mc.player.getVelocity().x, targetY, mc.player.getVelocity().z);
               mc.player.velocityDirty = true;
            }
         }
      }
   }

   private boolean isPlayerInWaterBlock() {
      BlockPos pos = BlockPos.ofFloored(mc.player.getX(), mc.player.getY(), mc.player.getZ());
      return mc.world.getBlockState(pos).isOf(Blocks.WATER)
         || mc.world.getBlockState(pos.down()).isOf(Blocks.WATER);
   }

   @Override
   public void onDisable() {
      this.jumpsUsed = 0;
      super.onDisable();
   }
}
