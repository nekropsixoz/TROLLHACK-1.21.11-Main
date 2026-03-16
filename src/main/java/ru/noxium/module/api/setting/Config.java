package ru.noxium.module.api.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Config {
   private final ArrayList<Setting> settingList = new ArrayList<>();

   public final void addSettings(Setting... options) {
      this.settingList.addAll(Arrays.asList(options));
   }

   public final List<Setting> getSettingsForGUI() {
      return this.settingList.stream().filter(setting -> !setting.hidden.get()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   public final List<Setting> getSettings() {
      return this.settingList;
   }
}
