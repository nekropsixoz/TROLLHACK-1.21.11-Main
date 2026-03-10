package lol.ethane.feature.scripting.wrapper.event;

import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.feature.scripting.wrapper.impl.DynamicLuaWrapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LuaMotionEvent extends DynamicLuaWrapper {
   public LuaMotionEvent(PlayerMovementTickEvent event) {
      this.register("state", () -> {
         return LuaValue.valueOf(event.getState().name());
      });
      this.register("ground", () -> {
         return LuaValue.valueOf(event.isGround());
      });
      this.registerMethod("cancel", new ZeroArgFunction() {
         public LuaValue call() {
            event.setCancelled();
            return LuaValue.NIL;
         }
      });
   }
}
