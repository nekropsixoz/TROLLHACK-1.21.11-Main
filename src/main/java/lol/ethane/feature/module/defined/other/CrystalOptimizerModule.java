package lol.ethane.feature.module.defined.other;

import lol.ethane.event.defined.network.SendPacketEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.utils.misc.crystal.InteractHandler;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_310;

public class CrystalOptimizerModule extends Module {
   private InteractHandler cachedHandler;

   public CrystalOptimizerModule() {
      super("Crystal Optimizer", "Optimizes the handling of using end crystals.", ModuleCategory.OTHER);
   }

   @Subscribe
   private void onPacket(SendPacketEvent event) {
      class_2596 var3 = event.getPacket();
      if (var3 instanceof class_2824) {
         class_2824 interactPacket = (class_2824)var3;
         if (this.cachedHandler == null) {
            this.cachedHandler = new InteractHandler(class_310.method_1551());
         }

         interactPacket.method_34209(this.cachedHandler);
      }

   }
}
