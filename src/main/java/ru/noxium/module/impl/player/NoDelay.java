package ru.noxium.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.mixin.ClientPlayerInteractionManagerAccessor;
import ru.noxium.mixin.MinecraftClientAccessor;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(
   name = "No Delay",
   description = "Убирает задержку",
   category = Category.Player,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class NoDelay extends Module {
   public static BooleanSetting jump = new BooleanSetting("Jump", true);
   public static BooleanSetting blockDestroy = new BooleanSetting("Block Destroy", false);
   public static BooleanSetting leftClick = new BooleanSetting("Left Click", false);
   public static BooleanSetting rightClick = new BooleanSetting("Right Click", false);
   public static SliderSetting jumpDelay = new SliderSetting("Jump Delay", 0.0F, 0.0F, 10.0F, 1.0F, false);
   public static SliderSetting blockDestroyDelay = new SliderSetting("Block Delay", 0.0F, 0.0F, 5.0F, 1.0F, false);
   public static SliderSetting leftClickDelay = new SliderSetting("LClick Delay", 0.0F, 0.0F, 10.0F, 1.0F, false);
   public static SliderSetting rightClickDelay = new SliderSetting("RClick Delay", 0.0F, 0.0F, 4.0F, 1.0F, false);

   public NoDelay() {
      this.addSettings(new Setting[]{jump, jumpDelay, blockDestroy, blockDestroyDelay, leftClick, leftClickDelay, rightClick, rightClickDelay});
   }

   @EventInit
   public void onEvent(EventUpdate e) {
      if (mc.player != null) {
         if (blockDestroy.get() && mc.interactionManager != null) {
            ((ClientPlayerInteractionManagerAccessor)mc.interactionManager).setBlockBreakingCooldown((int)blockDestroyDelay.get());
         }

         if (leftClick.get()) {
            ((MinecraftClientAccessor)mc).setAttackCooldown((int)leftClickDelay.get());
         }

         if (rightClick.get()) {
            ((MinecraftClientAccessor)mc).setItemUseCooldown((int)rightClickDelay.get());
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.interactionManager != null) {
         ((ClientPlayerInteractionManagerAccessor)mc.interactionManager).setBlockBreakingCooldown(5);
      }

      ((MinecraftClientAccessor)mc).setAttackCooldown(10);
      ((MinecraftClientAccessor)mc).setItemUseCooldown(4);
   }
}
