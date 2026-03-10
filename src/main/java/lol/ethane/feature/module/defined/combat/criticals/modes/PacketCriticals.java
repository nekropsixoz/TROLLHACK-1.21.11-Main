package lol.ethane.feature.module.defined.combat.criticals.modes;

import lol.ethane.event.defined.combat.AttackEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.combat.criticals.CriticalsModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import net.minecraft.class_2828.class_2830;

public class PacketCriticals extends ModuleMode<CriticalsModule> {
   public PacketCriticals(CriticalsModule module) {
      super(module);
   }

   @Subscribe
   private void onAttack(AttackEvent event) {
      this.mc.field_1724.field_3944.method_52787(new class_2830(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + 0.2D, this.mc.field_1724.method_23321(), this.mc.field_1724.method_36454(), this.mc.field_1724.method_36455(), false, this.mc.field_1724.field_5976));
      this.mc.field_1724.field_3944.method_52787(new class_2830(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + 0.01D, this.mc.field_1724.method_23321(), this.mc.field_1724.method_36454(), this.mc.field_1724.method_36455(), false, this.mc.field_1724.field_5976));
   }

   public Enum<?> getValue() {
      return CriticalsModule.Mode.PACKET;
   }
}
