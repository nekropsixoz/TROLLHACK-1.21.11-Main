package ru.noxium.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import ru.noxium.Noxium;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.Theme;

@Environment(EnvType.CLIENT)
public class GuiManager {
   public static MinecraftClient mc = MinecraftClient.getInstance();
   private File file;
   private Theme currentTheme = Theme.THEME1;
   private Category currentCategory = Category.Visuals;

   public void init() {
      this.file = new File(Noxium.get.root + "\\configs", "gui.cfg");

      try {
         if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdirs();
         }

         if (!this.file.exists()) {
            this.file.createNewFile();
            this.saveSettings();
         } else {
            this.readSettings();
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public void setGuiTheme(Theme theme) {
      this.currentTheme = theme;
      this.saveSettings();
   }

   public void setGuiCategory(Category category) {
      this.currentCategory = category;
      this.saveSettings();
   }

   public Theme getCurrentTheme() {
      return this.currentTheme;
   }

   public Category getCurrentCategory() {
      return this.currentCategory;
   }

   private void saveSettings() {
      try (FileWriter writer = new FileWriter(this.file)) {
         Properties props = new Properties();
         props.setProperty("theme", this.currentTheme.name());
         props.setProperty("category", this.currentCategory.name());
         props.store(writer, "GUI Settings");
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   private void readSettings() {
      try (FileReader reader = new FileReader(this.file)) {
         Properties props = new Properties();
         props.load(reader);
         this.currentTheme = Theme.valueOf(props.getProperty("theme", Theme.THEME1.name()));
         this.currentCategory = Category.valueOf(props.getProperty("category", Category.Visuals.name()));
      } catch (IllegalArgumentException | IOException var6) {
         var6.printStackTrace();
      }
   }
}
