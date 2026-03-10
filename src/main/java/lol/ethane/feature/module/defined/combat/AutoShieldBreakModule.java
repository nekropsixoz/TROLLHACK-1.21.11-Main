package lol.ethane.feature.module.defined.combat;

import lol.ethane.event.defined.combat.AttackEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.notification.Notification;
import lol.ethane.feature.helper.impl.notification.NotificationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2824;
import net.minecraft.class_2868;

public class AutoShieldBreakModule extends Module {
   private final Property<Boolean> notify = new BooleanProperty("Notify", false);

   public AutoShieldBreakModule() {
      super("Auto Shield Break", "Automatically breaks the shield on attack.", ModuleCategory.COMBAT);
      this.addProperties(new Property[]{this.notify});
   }

   @Subscribe
   private void onAttack(AttackEvent event) {
      class_1297 enemy = event.getTarget();
      if (enemy instanceof class_1657) {
         class_1657 player = (class_1657)enemy;
         if (player.method_6079().method_7909() == class_1802.field_8255) {
            if (player.method_6115()) {
               int slot = -1;

               for(int i = 0; i < 9; ++i) {
                  class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
                  if (stack.method_7909() instanceof class_1743) {
                     slot = i;
                     break;
                  }
               }

               if (slot != -1) {
                  this.mc.method_1562().method_52787(new class_2868(slot));
                  this.mc.method_1562().method_52787(class_2824.method_34206(enemy, this.mc.field_1724.method_5715()));
                  this.mc.field_1724.method_7350();
                  this.mc.field_1724.method_6104(class_1268.field_5808);
                  this.mc.method_1562().method_52787(new class_2868(this.mc.field_1724.method_31548().method_67532()));
                  event.setCancelled();
                  if ((Boolean)this.notify.getValue()) {
                     NotificationHelper.getInstance().queue(new Notification("Auto Shield Break", "Attempted to break the shield.", 3500L));
                  }

               }
            }
         }
      }
   }
}
