package ru.noxium.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;

@IModule(
   name = "No Push",
   description = "Убирает отталкивания от игроков, мобов и блоков",
   category = Category.Player,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class NoPush extends Module {
   public BooleanSetting players = new BooleanSetting("Players", true);
   public BooleanSetting mobs = new BooleanSetting("Mobs", true);
   public BooleanSetting blocks = new BooleanSetting("Blocks", true);

   public NoPush() {
      this.addSettings(new Setting[]{this.players, this.mobs, this.blocks});
   }
}
