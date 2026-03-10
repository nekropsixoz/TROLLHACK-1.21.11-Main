package lol.ethane.feature.scripting.wrapper.impl;

import lol.ethane.Ethane;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.UnknownModuleException;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaModulesWrapper extends DynamicLuaWrapper {
   public LuaModulesWrapper() {
      this.registerMethod("is_enabled", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaModulesWrapper) {
               start = 2;
            }

            String moduleName = args.checkjstring(start).toLowerCase().replace(" ", "");

            try {
               Module module = Ethane.getInstance().getModuleRepository().getModule(moduleName);
               return LuaValue.valueOf(module.isEnabled());
            } catch (UnknownModuleException var5) {
               return LuaValue.FALSE;
            }
         }
      });
      this.registerMethod("set_state", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaModulesWrapper) {
               start = 2;
            }

            String moduleName = args.checkjstring(start).toLowerCase().replace(" ", "");
            boolean state = args.checkboolean(start + 1);

            try {
               Module module = Ethane.getInstance().getModuleRepository().getModule(moduleName);
               module.setEnabled(state);
            } catch (UnknownModuleException var6) {
            }

            return LuaValue.NIL;
         }
      });
      this.registerMethod("toggle", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaModulesWrapper) {
               start = 2;
            }

            String moduleName = args.checkjstring(start).toLowerCase().replace(" ", "");

            try {
               Module module = Ethane.getInstance().getModuleRepository().getModule(moduleName);
               module.toggle();
            } catch (UnknownModuleException var5) {
            }

            return LuaValue.NIL;
         }
      });
   }
}
