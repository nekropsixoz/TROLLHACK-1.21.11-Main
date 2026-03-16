package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;

@IModule(
   name = "Hit Box",
   description = "Жозки авто свин",
   category = Category.Combat,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class HitBox extends Module {
   public static SliderSetting expand = new SliderSetting("Размер", 0.2F, 0.0F, 2.0F, 1.0F, false);
   public static BooleanSetting ignFr = new BooleanSetting("Игнор друзей", true);
   public HitBox() {
      this.addSettings(new Setting[]{expand, ignFr});
   }
}
