package lol.ethane.feature.module.defined.combat.velocity.modes;

import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.combat.velocity.VelocityModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2743;

public class ReversedVelocity extends ModuleMode<VelocityModule> {
   public ReversedVelocity(VelocityModule module) {
      super(module);
   }

   @Subscribe
   private void onPacket(ReceivePacketEvent event) {
      class_2596 var3 = event.getPacket();
      if (var3 instanceof class_2743) {
         class_2743 packet = (class_2743)var3;
         if (packet.method_11818() == this.mc.field_1724.method_5628()) {
            class_243 motion = packet.method_73085();
            this.mc.field_1724.method_5750(new class_243(motion.field_1352 * -1.0D, motion.field_1351, motion.field_1350 * -1.0D));
            event.setCancelled();
         }
      }

   }

   public Enum<?> getValue() {
      return VelocityModule.Mode.REVERSED;
   }
}
