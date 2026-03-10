package lol.ethane.mixin.network;

import java.util.Iterator;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.network.ReceivePacketEvent;
import lol.ethane.event.defined.network.SendPacketEvent;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2987;
import net.minecraft.class_8042;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2535.class})
public abstract class ConnectionMixin {
   @Shadow
   private static <T extends class_2547> void method_10759(class_2596<T> packet, class_2547 packetListener) {
   }

   @Inject(
      method = {"method_10743"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hookSendPacket(class_2596<?> packet, CallbackInfo ci) {
      SendPacketEvent event = new SendPacketEvent(packet);
      EventDispatcher.dispatch(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_10759"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 1
   )
   private static void hookReceivePacket(class_2596<?> packet, class_2547 listener, CallbackInfo ci) {
      if (!(packet instanceof class_8042)) {
         ReceivePacketEvent event = new ReceivePacketEvent(packet);
         EventDispatcher.dispatch(event);
         if (event.isCancelled()) {
            ci.cancel();
         }

      } else {
         class_8042 clientboundBundlePacket = (class_8042)packet;
         ci.cancel();
         Iterator var4 = clientboundBundlePacket.method_48324().iterator();

         while(var4.hasNext()) {
            class_2596 packetInBundle = (class_2596)var4.next();

            try {
               method_10759(packetInBundle, listener);
            } catch (class_2987 var7) {
            }
         }

      }
   }
}
