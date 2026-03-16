package ru.noxium.module.impl.visuals;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;

@IModule(
   name = "RTX Sounds",
   description = " ",
   category = Category.Visuals,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class RTXSounds extends Module {
   public static ModeSetting performancePriority = new ModeSetting("Качество звука", "Производительность", "Производительность", "Качество");
   public static BooleanSetting stereo = new BooleanSetting("3д стерео", true);
   public static BooleanSetting tone = new BooleanSetting("Тон", true);

   public RTXSounds() {
      this.addSettings(new Setting[]{performancePriority, stereo, tone});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         Noxium.rtx.updateMixer();
      }
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (Noxium.rtx != null) {
         Noxium.rtx.updateMixer();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Noxium.rtx.setState(false);
   }
}
