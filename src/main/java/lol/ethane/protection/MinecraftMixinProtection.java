package lol.ethane.protection;

import lol.ethane.Ethane;

public class MinecraftMixinProtection {
   public static void init() {
      Ethane.setInstance();
      Ethane.getInstance().runInitializations();
   }

   public static void shutdown() {
      Ethane.getInstance().shutdown();
   }
}
