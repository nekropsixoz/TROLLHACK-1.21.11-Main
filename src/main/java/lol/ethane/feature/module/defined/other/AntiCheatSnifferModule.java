package lol.ethane.feature.module.defined.other;

import lol.ethane.feature.helper.impl.ServerObserver;
import lol.ethane.feature.helper.impl.notification.Notification;
import lol.ethane.feature.helper.impl.notification.NotificationHelper;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;

public class AntiCheatSnifferModule extends Module {
   public AntiCheatSnifferModule() {
      super("Anti Cheat Sniffer", "Attempts to guess the anticheat the server is using.", ModuleCategory.OTHER);
   }

   protected void onEnable() {
      NotificationHelper.getInstance().queue(new Notification("Anti Cheat Sniffer", ServerObserver.get().guessAntiCheat(), 4000L));
      this.toggle();
   }
}
