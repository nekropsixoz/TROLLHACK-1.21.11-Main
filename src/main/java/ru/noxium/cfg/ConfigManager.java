package ru.noxium.cfg;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.FilenameUtils;
import ru.noxium.Noxium;

@Environment(EnvType.CLIENT)
public final class ConfigManager extends Manager<Config> {
   private static final String OS = System.getProperty("os.name").toLowerCase();
   private static final boolean IS_WINDOWS = OS.contains("win");
   private static final boolean IS_LINUX = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
   public static final File configDirectory = getConfigDirectory();
   private static final ArrayList<Config> loadedConfigs = new ArrayList<>();

   private static File getConfigDirectory() {
      if (IS_LINUX) {
         String homeDir = System.getProperty("user.home");
         return new File(homeDir, ".noxiumdlc" + File.separator + "configs" + File.separator + "cfg");
      } else {
         return new File(Noxium.get.root, "configs" + File.separator + "cfg");
      }
   }

   public ConfigManager() {
      this.setContents(loadConfigs());
      configDirectory.mkdirs();
   }

   private static ArrayList<Config> loadConfigs() {
      File[] files = configDirectory.listFiles();
      if (files != null) {
         for (File file : files) {
            if (FilenameUtils.getExtension(file.getName()).equals("json")) {
               loadedConfigs.add(new Config(FilenameUtils.removeExtension(file.getName())));
            }
         }
      }

      return loadedConfigs;
   }

   public static ArrayList<Config> getLoadedConfigs() {
      return loadedConfigs;
   }

   public void load() {
      if (!configDirectory.exists()) {
         configDirectory.mkdirs();
      }

      if (configDirectory != null) {
         File[] files = configDirectory.listFiles(fx -> !fx.isDirectory() && FilenameUtils.getExtension(fx.getName()).equals("json"));

         for (File f : files) {
            Config config = new Config(FilenameUtils.removeExtension(f.getName()).replace(" ", ""));
            loadedConfigs.add(config);
         }
      }
   }

   public boolean loadConfig(String configName) {
      if (configName == null) {
         return false;
      } else {
         Config config = this.findConfig(configName);
         if (config == null) {
            return false;
         } else {
            try {
               boolean var6;
               try (FileReader reader = new FileReader(config.getFile())) {
                  JsonParser parser = new JsonParser();
                  JsonObject object = (JsonObject)parser.parse(reader);
                  config.load(object);
                  var6 = true;
               }

               return var6;
            } catch (IOException var9) {
               return false;
            }
         }
      }
   }

   public boolean saveConfig(String configName) {
      if (configName == null) {
         return false;
      } else {
         Config config;
         if ((config = this.findConfig(configName)) == null) {
            Config newConfig = config = new Config(configName);
            this.getContents().add(newConfig);
         }

         String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(config.save());

         try {
            boolean var5;
            try (FileWriter writer = new FileWriter(config.getFile())) {
               writer.write(contentPrettyPrint);
               var5 = true;
            }

            return var5;
         } catch (IOException var9) {
            return false;
         }
      }
   }

   public Config findConfig(String configName) {
      if (configName == null) {
         return null;
      } else {
         for (Config config : this.getContents()) {
            if (config.getName().equalsIgnoreCase(configName)) {
               return config;
            }
         }

         return new File(configDirectory, configName + ".json").exists() ? new Config(configName) : null;
      }
   }

   public boolean deleteConfig(String configName) {
      if (configName == null) {
         return false;
      } else {
         Config config;
         if ((config = this.findConfig(configName)) == null) {
            return false;
         } else {
            File f = config.getFile();
            this.getContents().remove(config);
            return f.exists() && f.delete();
         }
      }
   }

   public void autoSave() {
      if (Noxium.get.configManager != null) {
         this.saveConfig("default");
      }
   }
}
