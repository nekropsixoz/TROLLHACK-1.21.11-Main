package lol.ethane.feature.scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import lol.ethane.Ethane;
import lol.ethane.feature.scripting.wrapper.impl.LuaClientWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaMathWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaModuleWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaModulesWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaPlayerWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaRenderWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaWorldWrapper;
import lol.ethane.feature.scripting.wrapper.module.LuaModule;
import lol.ethane.utils.data.FileUtil;
import lol.ethane.utils.misc.ChatUtil;
import lombok.Generated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.script.LuaScriptEngine;

public class Script {
   @Generated
   private static final Logger log = LogManager.getLogger(Script.class);
   private static final String NAME_HEADER = "--name:";
   private final LuaScriptEngine scriptEngine;
   private final File file;
   private final String content;
   private final String name;
   private final List<LuaModule> modules = new ArrayList();

   public Script(File file) {
      ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("luaj");
      if (!(engine instanceof LuaScriptEngine)) {
         throw new RuntimeException("Lua script engine could not be initialized");
      } else {
         LuaScriptEngine luaScriptEngine = (LuaScriptEngine)engine;
         this.scriptEngine = luaScriptEngine;
         this.populateEngine();
         this.file = file;
         try {
            this.content = FileUtil.readFile(file);
         } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read script file", e);
         }
         if (this.content != null && !this.content.isEmpty()) {
            String discoveredName = null;
            String[] var5 = this.content.split("\n");
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String line = var5[var7];
               if (line.startsWith("--name:")) {
                  discoveredName = line.substring("--name:".length()).trim();
                  break;
               }
            }

            if (discoveredName == null) {
               throw new RuntimeException("Script needs to have a name header!");
            } else {
               this.name = discoveredName.replace(" ", "");
            }
         } else {
            throw new RuntimeException("Script can't be an empty file!");
         }
      }
   }

   public void load() {
      try {
         this.scriptEngine.eval(this.content);
      } catch (ScriptException var2) {
         String var10000 = this.name;
         ChatUtil.sendErrorMessage("Failed to evaluate script " + var10000 + ": " + var2.getMessage());
         log.error("Failed to evaluate script {}", this.name);
         var2.printStackTrace();
      }

   }

   public void unload() {
      Iterator var1 = this.modules.iterator();

      while(var1.hasNext()) {
         LuaModule module = (LuaModule)var1.next();
         Ethane.getInstance().getModuleRepository().unregister(module);
      }

      this.modules.clear();
   }

   private void populateEngine() {
      Bindings bindings = new SimpleBindings();
      bindings.put("api_version", "1.0.0");
      bindings.put("client", new LuaClientWrapper());
      bindings.put("modules", new LuaModulesWrapper());
      bindings.put("render", new LuaRenderWrapper());
      bindings.put("player", new LuaPlayerWrapper());
      bindings.put("world", new LuaWorldWrapper());
      bindings.put("math", new LuaMathWrapper());
      this.scriptEngine.setBindings(bindings, 100);
      this.scriptEngine.put("registerModule", new LibFunction() {
         public LuaValue call(LuaValue name, LuaValue description, LuaValue category) {
            if (name.isstring() && description.isstring() && category.isstring()) {
               LuaModule module = new LuaModule(name.tojstring(), description.tojstring(), category.tojstring());
               Script.this.modules.add(module);
               Ethane.getInstance().getModuleRepository().register(module);
               return new LuaModuleWrapper(module);
            } else {
               return LuaValue.error("name, description and category need to be strings!");
            }
         }
      });
   }

   @Generated
   public LuaScriptEngine getScriptEngine() {
      return this.scriptEngine;
   }

   @Generated
   public File getFile() {
      return this.file;
   }

   @Generated
   public String getContent() {
      return this.content;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public List<LuaModule> getModules() {
      return this.modules;
   }
}
