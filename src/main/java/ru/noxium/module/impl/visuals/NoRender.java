package ru.noxium.module.impl.visuals;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffects;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.impl.BooleanSetting;

@IModule(name = "No Render", description = "Убирает различные визуальные эффекты", category = Category.Visuals, bind = -1)
@Environment(EnvType.CLIENT)
public class NoRender extends Module {

   public static BooleanSetting fire = new BooleanSetting("Огонь", true);
   public static BooleanSetting shake = new BooleanSetting("Тряска", true);

   public NoRender() {
      this.addSettings(fire, shake);
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (mc.player != null) {
         if (mc.player.hasStatusEffect(StatusEffects.DARKNESS)) {
            mc.player.removeStatusEffect(StatusEffects.DARKNESS);
         }
      }
   }
}
