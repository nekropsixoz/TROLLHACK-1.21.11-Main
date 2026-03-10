package lol.ethane.feature.scripting.wrapper.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

public abstract class DynamicLuaWrapper extends LuaTable {
   private final Map<String, Supplier<LuaValue>> dynamicGetters = new HashMap();
   private final Map<String, LibFunction> methods = new HashMap();

   protected void register(String name, Supplier<LuaValue> getter) {
      this.dynamicGetters.put(name, getter);
   }

   protected void registerMethod(String name, LibFunction function) {
      this.methods.put(name, function);
   }

   public LuaValue get(LuaValue key) {
      if (key.isstring()) {
         String keyString = key.tojstring();
         Supplier<LuaValue> getter = (Supplier)this.dynamicGetters.get(keyString);
         if (getter != null) {
            return (LuaValue)getter.get();
         }

         LibFunction method = (LibFunction)this.methods.get(keyString);
         if (method != null) {
            return method;
         }
      }

      return super.get(key);
   }
}
