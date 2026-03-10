package lol.ethane.feature.scripting.wrapper.module;

import java.util.HashMap;
import java.util.Map;
import lol.ethane.event.defined.combat.AttackEvent;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.defined.network.SendPacketEvent;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.scripting.wrapper.Scripting;
import lol.ethane.feature.scripting.wrapper.event.LuaAttackEvent;
import lol.ethane.feature.scripting.wrapper.event.LuaMotionEvent;
import lol.ethane.feature.scripting.wrapper.event.LuaPacketEvent;
import lol.ethane.feature.scripting.wrapper.event.LuaRender2DEvent;
import lol.ethane.feature.scripting.wrapper.event.LuaTickEvent;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

@Scripting
public class LuaModule extends Module {
   private final Map<String, LuaClosure> eventHandlers = new HashMap();

   public LuaModule(String name, String description, String category) {
      super(name, description, ModuleCategory.valueOf(category.toUpperCase()));
   }

   public void onEnable() {
      this.callEvent("enable", (Object)null);
      super.onEnable();
   }

   public void onDisable() {
      this.callEvent("disable", (Object)null);
      super.onDisable();
   }

   @Subscribe
   private void onRender2D(Render2DEvent event) {
      this.callEvent("render2d", event);
   }

   @Subscribe
   private void onTick(PreGameTickEvent event) {
      this.callEvent("tick", event);
   }

   @Subscribe
   private void onMotion(PlayerMovementTickEvent event) {
      this.callEvent("motion", event);
   }

   @Subscribe
   private void onPacketSend(SendPacketEvent event) {
      this.mc.execute(() -> {
         this.callEvent("packet_send", event);
      });
   }

   @Subscribe
   private void onPacketReceive(ReceivePacketEvent event) {
      this.mc.execute(() -> {
         this.callEvent("packet_receive", event);
      });
   }

   @Subscribe
   private void onAttack(AttackEvent event) {
      this.callEvent("attack", event);
   }

   public void event_callback(String eventName, LuaClosure function) {
      this.eventHandlers.put(eventName, function);
   }

   private void callEvent(String name, Object event) {
      LuaClosure function = (LuaClosure)this.eventHandlers.getOrDefault(name, null);
      if (function != null) {
         try {
            function.call(javaEventToLua(event));
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

   }

   private static LuaValue javaEventToLua(Object event) {
      if (event instanceof Render2DEvent render2DEvent) {
         return new LuaRender2DEvent(render2DEvent);
      } else if (event instanceof PreGameTickEvent preGameTickEvent) {
         return new LuaTickEvent(preGameTickEvent);
      } else if (event instanceof PlayerMovementTickEvent playerMovementTickEvent) {
         return new LuaMotionEvent(playerMovementTickEvent);
      } else if (event instanceof SendPacketEvent sendPacketEvent) {
         return new LuaPacketEvent(sendPacketEvent, sendPacketEvent.getPacket());
      } else if (event instanceof ReceivePacketEvent receivePacketEvent) {
         return new LuaPacketEvent(receivePacketEvent, receivePacketEvent.getPacket());
      } else if (event instanceof AttackEvent attackEvent) {
         return new LuaAttackEvent(attackEvent);
      }
      
      return LuaValue.NIL;
   }
}
