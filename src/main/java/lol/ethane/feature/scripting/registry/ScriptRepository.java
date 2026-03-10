package lol.ethane.feature.scripting.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lol.ethane.feature.scripting.Script;
import lol.ethane.feature.scripting.wrapper.module.LuaModule;
import lol.ethane.utils.math.Measurement;
import lol.ethane.utils.misc.ChatUtil;
import lombok.Generated;
import net.minecraft.class_310;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptRepository implements IScriptRepository<Script> {
   @Generated
   private static final Logger log = LogManager.getLogger(ScriptRepository.class);
   public static final String API_VERSION = "1.0.0";
   private final Map<String, Script> scriptMap = new ConcurrentHashMap();
   private File directory;

   public void init() {
      this.directory = new File(class_310.method_1551().field_1697, File.separator + "ethane" + File.separator + "scripts" + File.separator);
      if (!this.directory.exists()) {
         boolean result = this.directory.mkdir();
         log.info("Created {} {}", this.directory, result ? "successfully" : "unsuccessfully");
      }

      log.info("Loading scripts from {}", this.directory);
      this.loadScripts();
   }

   public void loadScripts() {
      this.unloadScripts();
      File[] files = this.directory.listFiles();
      if (files != null && files.length != 0) {
         File[] var2 = files;
         int var3 = files.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file.getName().endsWith(".lua")) {
               this.loadScript(file);
            }
         }

      } else {
         log.info("No scripts to load");
      }
   }

   public void loadScript(File file) {
      try {
         Measurement.begin(() -> {
            this.add(new Script(file));
         }, log, "Script " + file.getName() + " evaluated in {}");
      } catch (Exception var3) {
         String var10000 = file.getName();
         ChatUtil.sendErrorMessage("Failed to load script " + var10000 + ": " + var3.getMessage());
         log.error("Failed to load a script from file {}", file);
         var3.printStackTrace();
      }

   }

   public void unloadScripts() {
      Iterator var1 = this.scriptMap.values().iterator();

      while(var1.hasNext()) {
         Script script = (Script)var1.next();
         this.unloadScript(script);
      }

   }

   public void unloadScript(Script script) {
      Measurement.begin(() -> {
         this.remove(script);
      }, log, "Unloaded script file " + String.valueOf(script.getFile()) + " in {}");
   }

   public void reloadScript(Script script) {
      File file = script.getFile();
      this.unloadScript(script);
      this.loadScript(file);
   }

   public Script get(String name) {
      return (Script)this.scriptMap.getOrDefault(name, null);
   }

   public void add(Script... elements) {
      Script[] var2 = elements;
      int var3 = elements.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Script script = var2[var4];
         Script existing = (Script)this.scriptMap.get(script.getName());
         List<String> enabledModuleIds = new ArrayList();
         Iterator var8;
         if (existing != null) {
            var8 = existing.getModules().iterator();

            while(var8.hasNext()) {
               LuaModule module = (LuaModule)var8.next();
               if (module.isEnabled()) {
                  enabledModuleIds.add(module.getId());
               }
            }

            existing.unload();
         }

         script.load();
         var8 = enabledModuleIds.iterator();

         while(var8.hasNext()) {
            String id = (String)var8.next();
            Iterator var10 = script.getModules().iterator();

            while(var10.hasNext()) {
               LuaModule module = (LuaModule)var10.next();
               if (module.getId().equals(id)) {
                  module.setEnabled(true);
               }
            }
         }

         this.scriptMap.put(script.getName(), script);
      }

   }

   public void remove(Script... elements) {
      Script[] var2 = elements;
      int var3 = elements.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Script script = var2[var4];
         script.unload();
         this.scriptMap.remove(script.getName());
      }

   }

   public int size() {
      return this.scriptMap.size();
   }

   public Collection<Script> values() {
      return this.scriptMap.values();
   }
}
