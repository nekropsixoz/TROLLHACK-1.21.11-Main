package ru.noxium.module.impl.visuals;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.noxium.event.EventInit;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;

@IModule(name = "FullBright", description = " ", category = Category.Visuals, bind = -1)
@Environment(EnvType.CLIENT)
public class NightVision extends Module {
   public ModeSetting mode = new ModeSetting("Тип", "Гамма", "Гамма", "Эффект");

   public NightVision() {
      this.addSettings(new Setting[] { this.mode });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.worldRenderer != null && this.mode.is("Гамма")) {
         mc.worldRenderer.reload();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.worldRenderer != null && this.mode.is("Гамма")) {
         mc.worldRenderer.reload();
      }

      if (this.mode.is("Эффект")) {
         mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
      }
   }

   @EventInit
   public void onUpdate(ClientTickEvent e) {
      if (mc.player != null) {
         if (this.mode.is("Гамма")) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         }

         if (this.mode.is("Эффект")) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, false, false));
         }
      }
   }
}
