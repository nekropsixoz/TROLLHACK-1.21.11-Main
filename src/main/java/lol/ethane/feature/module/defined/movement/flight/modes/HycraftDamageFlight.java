package lol.ethane.feature.module.defined.movement.flight.modes;

import java.util.ArrayList;
import java.util.List;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.flight.FlightModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2743;
import net.minecraft.class_634;

public class HycraftDamageFlight extends ModuleMode<FlightModule> {
   private boolean damageTaken = false;
   private boolean release = false;
   private int ticks = 0;
   private final List<class_2596<class_634>> packets = new ArrayList();

   public HycraftDamageFlight(FlightModule module) {
      super(module);
   }

   public void onEnable() {
      this.ticks = 0;
      this.damageTaken = false;
      this.release = false;
      this.packets.clear();
      super.onEnable();
   }

   public void onDisable() {
      if (this.mc.method_1562() != null && !this.packets.isEmpty()) {
         List<class_2596<class_634>> packetsToRelease = new ArrayList(this.packets);
         this.packets.clear();
         this.mc.execute(() -> {
            if (this.mc.method_1562() != null) {
               packetsToRelease.forEach((p) -> {
                  try {
                     p.method_65081(this.mc.method_1562());
                  } catch (Exception var3) {
                  }

               });
            }

         });
      }

      super.onDisable();
   }

   @Subscribe
   private void onTick(PreGameTickEvent event) {
      if (this.ticks > 0) {
         this.ticks--;
      }

   }

   @Subscribe
   @SuppressWarnings("unchecked")
   private void onPacket(ReceivePacketEvent event) {
      class_2596<?> packet = event.getPacket();
      
      if (packet instanceof class_2743) {
         class_2743 velocityPacket = (class_2743)packet;
         if (velocityPacket.method_11818() == this.mc.field_1724.method_5628()) {
            if (!this.damageTaken) {
               this.damageTaken = true;
               this.ticks = 40;
            } else {
               this.damageTaken = false;
               this.release = true;
            }
            this.packets.add((class_2596<class_634>)packet);
            event.setCancelled();
            return;
         }
      }

      if (packet instanceof class_2561) {
         if (this.ticks <= 0) {
            if (this.release) {
               ((FlightModule)this.module).setEnabled(false);
            }
            return;
         }

         this.ticks--;
         this.packets.add((class_2596<class_634>)packet);
         event.setCancelled();
      }

   }

   public Enum<?> getValue() {
      return FlightModule.Mode.HYCRAFT_DAMAGE;
   }
}
