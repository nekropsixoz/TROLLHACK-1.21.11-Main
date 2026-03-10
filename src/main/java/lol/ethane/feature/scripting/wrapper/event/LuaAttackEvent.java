package lol.ethane.feature.scripting.wrapper.event;

import lol.ethane.event.defined.combat.AttackEvent;
import lol.ethane.feature.scripting.wrapper.impl.DynamicLuaWrapper;
import lol.ethane.feature.scripting.wrapper.impl.LuaEntityWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaAttackEvent extends DynamicLuaWrapper {
   public LuaAttackEvent(AttackEvent event) {
      this.register("target", () -> {
         return new LuaEntityWrapper(event.getTarget());
      });
      this.registerMethod("cancel", new ZeroArgFunction() {
         public LuaValue call() {
            event.setCancelled();
            return LuaValue.NIL;
         }
      });
   }
}
