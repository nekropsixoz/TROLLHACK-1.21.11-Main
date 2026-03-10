package lol.ethane.feature.module.defined.other.disabler.modes;

import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.ConcurrentLinkedQueue;
import lol.ethane.event.defined.game.GameSwitchWorldEvent;
import lol.ethane.event.defined.network.SendPacketEvent;
import lol.ethane.event.defined.player.PlayerMovementTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.notification.Notification;
import lol.ethane.feature.helper.impl.notification.NotificationHelper;
import lol.ethane.feature.module.defined.other.disabler.DisablerModule;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;
import lol.ethane.mixin.accessor.ServerboundPlayerMovePacketAccessor;
import lol.ethane.utils.math.MovementUtil;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_10185;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2851;
import net.minecraft.class_6374;
import net.minecraft.class_2828.class_5911;

public class CubeCraftDisabler extends ModuleMode<DisablerModule> {
   public Property<CubeCraftDisabler.Type> typeProperty;
   private final Property<Boolean> spoofProperty;
   private int aTick;
   private int delayInterval;
   private boolean notified;
   private boolean notified2;
   private long startTime;
   private final ConcurrentLinkedQueue<class_2596<?>> queuedPackets;

   public CubeCraftDisabler(DisablerModule module) {
      super(module);
      this.typeProperty = new EnumProperty("Type", this, CubeCraftDisabler.Type.A);
      this.spoofProperty = new BooleanProperty("Spoof ground", this, true);
      this.queuedPackets = new ConcurrentLinkedQueue();
   }

   public void onEnable() {
      this.aTick = 0;
      this.startTime = 0L;
      this.delayInterval = 5000;
      super.onEnable();
   }

   @Subscribe
   private void onWorldSwitch(GameSwitchWorldEvent e) {
      this.aTick = 0;
      this.notified = this.notified2 = false;
      this.queuedPackets.clear();
   }

   @Subscribe
   private void onMotionTick(PlayerMovementTickEvent e) {
      if (this.startTime == 0L) {
         this.startTime = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() - this.startTime >= (long)this.delayInterval) {
         ChatUtil.sendMessage("released " + this.queuedPackets.size());

         while(!this.queuedPackets.isEmpty()) {
            class_2596<?> packet = (class_2596)this.queuedPackets.poll();
            if (packet != null) {
               this.mc.method_1562().method_48296().method_10752(packet, (ChannelFutureListener)null);
            }
         }

         this.queuedPackets.clear();
         this.startTime = 0L;
      }

   }

   @Subscribe
   private void onSendPacket(SendPacketEvent event) {
      if (this.mc.field_1724 != null) {
         class_2596 var3 = event.getPacket();
         if (var3 instanceof class_2828) {
            class_2828 packet = (class_2828)var3;
            if ((Boolean)this.spoofProperty.getValue()) {
               ((ServerboundPlayerMovePacketAccessor)packet).setY(packet.method_12268(0.0D) - packet.method_12268(0.0D) % 0.015625D);
               ((ServerboundPlayerMovePacketAccessor)packet).setGround(true);
            }
         }

         class_6374 pongPacket;
         switch(((CubeCraftDisabler.Type)this.typeProperty.getValue()).ordinal()) {
         case 0:
            if (this.mc.field_1724.field_6012 <= 20) {
               if (!this.notified) {
                  NotificationHelper.getInstance().queue(new Notification("Disabler", "Please wait, disabling.", 5000L));
                  this.notified = true;
               }

               return;
            }

            if (this.aTick == 1) {
               ++this.aTick;
            }

            if (this.aTick >= 3) {
               if (event.getPacket() instanceof class_2851) {
                  event.setCancelled();
                  this.mc.method_1562().method_48296().method_10752(new class_2851(new class_10185(false, false, false, false, false, false, false)), (ChannelFutureListener)null);
               }

               if ((this.aTick >= 328 || this.aTick <= 5) && event.getPacket() instanceof class_2828) {
                  event.setCancelled();
               } else if (this.aTick % 55 == 0 && event.getPacket() instanceof class_2828) {
                  event.setCancelled();
                  this.mc.method_1562().method_48296().method_10752(new class_5911(true, this.mc.field_1724.field_5976), (ChannelFutureListener)null);
                  if (!this.notified2) {
                     NotificationHelper.getInstance().queue(new Notification("Disabler", "Finished!", 3000L));
                     this.notified2 = true;
                  }
               }
            }

            if (event.getPacket() instanceof class_2828 && Math.sqrt(this.mc.field_1724.method_5649(this.mc.field_1724.field_6038, this.mc.field_1724.field_5971, this.mc.field_1724.field_5989)) <= 8.0D - MovementUtil.speed() - 0.15D && this.mc.field_1724.field_6012 % 2 != 0) {
               event.setCancelled();
            }

            var3 = event.getPacket();
            if (var3 instanceof class_6374) {
               pongPacket = (class_6374)var3;
               int id = pongPacket.method_36960();
               if (id < 0 && (this.aTick >= 3 || this.aTick == 0)) {
                  ++this.aTick;
                  this.mc.method_1562().method_48296().method_10752(new class_6374(id + 1), (ChannelFutureListener)null);
               } else if (id != 0) {
                  event.setCancelled();
                  if (this.aTick >= 330 || this.aTick == 2) {
                     this.mc.method_1562().method_48296().method_10752(new class_6374(id + 3), (ChannelFutureListener)null);
                     this.aTick = 3;
                  }
               }
            }
            break;
         case 1:
            var3 = event.getPacket();
            if (var3 instanceof class_6374) {
               pongPacket = (class_6374)var3;
               if (this.startTime > 0L && pongPacket.method_36960() > 0 && this.mc.field_1724.field_6012 % 2 == 0) {
                  return;
               }

               if (this.mc.field_1724.field_6012 >= 20 && this.mc.field_1724.field_6012 % 100 == 0) {
                  this.mc.method_1562().method_48296().method_10752(new class_6374(pongPacket.method_36960() + 1), (ChannelFutureListener)null);
                  ChatUtil.sendMessage("sent forward");
               }
            }

            if (event.getPacket() instanceof class_2828 && Math.sqrt(this.mc.field_1724.method_5649(this.mc.field_1724.field_6038, this.mc.field_1724.field_5971, this.mc.field_1724.field_5989)) <= 8.0D - MovementUtil.speed() - 0.15D && this.mc.field_1724.field_6012 % 2 != 0 && this.mc.field_1724.field_6017 > 1.0D) {
               ChatUtil.sendMessage(String.valueOf(8.0D - MovementUtil.speed() - 0.15D));
               event.setCancelled();
            }
         }

      }
   }

   public Enum<?> getValue() {
      return DisablerModule.Mode.CUBE$CRAFT;
   }

   public static enum Type {
      A,
      B;

      // $FF: synthetic method
      private static CubeCraftDisabler.Type[] $values() {
         return new CubeCraftDisabler.Type[]{A, B};
      }
   }
}
