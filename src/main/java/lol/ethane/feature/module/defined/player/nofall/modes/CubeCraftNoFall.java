package lol.ethane.feature.module.defined.player.nofall.modes;

import lol.ethane.event.defined.network.SendPacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.player.nofall.NoFallModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.mixin.accessor.ServerboundPlayerMovePacketAccessor;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2350.class_2351;

public class CubeCraftNoFall extends ModuleMode<NoFallModule> {
   public CubeCraftNoFall(NoFallModule module) {
      super(module);
   }

   @Subscribe
   private void onSendPacket(SendPacketEvent event) {
      class_2596 var3 = event.getPacket();
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         if (this.mc.field_1724.field_6017 >= 2.5D) {
            ((ServerboundPlayerMovePacketAccessor)packet).setY(packet.method_12268(0.0D) - packet.method_12268(0.0D) % 0.015625D);
            ((ServerboundPlayerMovePacketAccessor)packet).setGround(true);
            this.mc.field_1724.method_5814(this.mc.field_1724.field_6014, this.mc.field_1724.field_6036, this.mc.field_1724.field_5969);
            this.mc.field_1724.method_18799(this.mc.field_1724.method_18798().method_38499(class_2351.field_11052, this.mc.field_1724.method_18798().field_1351 + 0.1D));
            this.mc.field_1724.field_6017 = 0.0D;
         }
      }

   }

   public Enum<?> getValue() {
      return NoFallModule.Mode.CUBE$CRAFT;
   }
}
