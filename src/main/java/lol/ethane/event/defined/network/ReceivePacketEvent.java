package lol.ethane.event.defined.network;

import lol.ethane.event.EventCancellable;
import lombok.Generated;
import net.minecraft.class_2596;

public class ReceivePacketEvent extends EventCancellable {
   private final class_2596<?> packet;

   @Generated
   public class_2596<?> getPacket() {
      return this.packet;
   }

   @Generated
   public ReceivePacketEvent(class_2596<?> packet) {
      this.packet = packet;
   }
}
