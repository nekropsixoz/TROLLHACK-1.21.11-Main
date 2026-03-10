package lol.ethane.feature.module.defined.movement.flight.modes;

import java.util.ArrayList;
import java.util.List;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.movement.flight.FlightModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2596;
import net.minecraft.class_634;

public class PolarFlight extends ModuleMode<FlightModule> {
   private long test;
   private final List<class_2596<class_634>> packets = new ArrayList();

   public PolarFlight(FlightModule module) {
      super(module);
   }

   public void onEnable() {
      this.packets.clear();
      this.test = System.currentTimeMillis();
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
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         this.mc.field_1687.method_8652(new class_2338(this.mc.field_1724.method_31477(), this.mc.field_1724.method_31478() - 1, this.mc.field_1724.method_31479()), class_2246.field_10375.method_9564(), 3);
      } else {
         ((FlightModule)this.module).setEnabled(false);
      }
   }

   @Subscribe
   @SuppressWarnings("unchecked")
   private void onPacket(ReceivePacketEvent event) {
      if (System.currentTimeMillis() - this.test > 750L) {
         this.packets.add((class_2596<class_634>)event.getPacket());
         event.setCancelled();
      }
   }

   public Enum<?> getValue() {
      return FlightModule.Mode.POLAR;
   }
}
