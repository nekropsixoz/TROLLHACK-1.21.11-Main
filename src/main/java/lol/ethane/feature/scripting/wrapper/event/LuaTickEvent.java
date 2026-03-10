package lol.ethane.feature.scripting.wrapper.event;

import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.feature.scripting.wrapper.impl.DynamicLuaWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaTickEvent extends DynamicLuaWrapper {
   public LuaTickEvent(PreGameTickEvent event) {
      this.registerMethod("cancel", new ZeroArgFunction() {
         public LuaValue call() {
            event.setCancelled();
            return LuaValue.NIL;
         }
      });
   }
}
