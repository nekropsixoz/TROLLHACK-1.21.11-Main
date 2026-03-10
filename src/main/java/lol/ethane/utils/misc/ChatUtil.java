package lol.ethane.utils.misc;

import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_338;

public final class ChatUtil {
   private ChatUtil() {
   }

   public static void sendMessage(String message) {
      if (class_310.method_1551().field_1724 != null) {
         class_310.method_1551().execute(() -> {
            class_338 var10000 = class_310.method_1551().field_1705.method_1743();
            String var10001 = String.valueOf(class_124.field_1078);
            var10000.method_1812(class_2561.method_43470(var10001 + "Ethane " + String.valueOf(class_124.field_1080) + "> " + String.valueOf(class_124.field_1068) + message));
         });
      }

   }

   public static void sendErrorMessage(String message) {
      if (class_310.method_1551().field_1724 != null) {
         class_310.method_1551().execute(() -> {
            class_338 var10000 = class_310.method_1551().field_1705.method_1743();
            String var10001 = String.valueOf(class_124.field_1061);
            var10000.method_1812(class_2561.method_43470(var10001 + "Error " + String.valueOf(class_124.field_1080) + "> " + String.valueOf(class_124.field_1068) + message));
         });
      }

   }
}
