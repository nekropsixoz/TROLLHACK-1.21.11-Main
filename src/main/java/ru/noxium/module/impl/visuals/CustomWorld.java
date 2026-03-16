package ru.noxium.module.impl.visuals;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.HueSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(name = "Custom World", category = Category.Visuals, description = "Render ESP on Player", bind = -1)
@Environment(EnvType.CLIENT)
public class CustomWorld extends Module {
   public static ModeSetting timeOfDay = new ModeSetting("Время суток", "Ночь", "День", "Закат", "Рассвет", "Ночь",
         "Полночь", "Полдень");
   public static BooleanSetting useFog = new BooleanSetting("Туман", true);
   public static HueSetting fogColor = new HueSetting("Цвет тумана", 15.0F, 1.0F, 1.0F);
   public static SliderSetting fogDistance = new SliderSetting("Дистанция тумана", 1.0F, 0.0F, 1.0F, 0.01F, false);
   public static long customTime = -1L;
   public static boolean isEnabled = false;

   public CustomWorld() {
      this.addSettings(new Setting[] { timeOfDay, useFog, fogColor, fogDistance });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      isEnabled = true;
      this.updateTargetTime();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      isEnabled = false;
      customTime = -1L;
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (isEnabled && mc.world != null) {
         this.updateTargetTime();
      }
   }

   private void updateTargetTime() {
      String currentMode = timeOfDay.get();
      switch (currentMode) {
         case "День":
            customTime = 1000L;
            break;
         case "Закат":
            customTime = 12000L;
            break;
         case "Рассвет":
            customTime = 23000L;
            break;
         case "Полночь":
            customTime = 13000L;
            break;
         case "Ночь":
            customTime = 18000L;
            break;
         case "Полдень":
            customTime = 6000L;
            break;
         default:
            customTime = 0L;
      }
   }
}
