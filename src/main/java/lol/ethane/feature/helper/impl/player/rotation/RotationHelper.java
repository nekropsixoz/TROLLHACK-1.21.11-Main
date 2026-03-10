package lol.ethane.feature.helper.impl.player.rotation;

import lol.ethane.feature.helper.impl.player.rotation.handler.RotationClientHandler;
import lol.ethane.feature.helper.impl.player.rotation.handler.RotationMouseHandler;
import lombok.Generated;

public final class RotationHelper {
   private static final RotationMouseHandler handler = new RotationMouseHandler();
   private static final RotationClientHandler clientHandler = new RotationClientHandler();

   private RotationHelper() {
   }

   @Generated
   public static RotationMouseHandler getHandler() {
      return handler;
   }

   @Generated
   public static RotationClientHandler getClientHandler() {
      return clientHandler;
   }
}
