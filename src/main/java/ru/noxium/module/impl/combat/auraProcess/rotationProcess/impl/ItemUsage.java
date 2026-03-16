package ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl;

import java.util.Objects;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventPacket;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.Component;
import ru.noxium.util.other.IMinecraft;

@Environment(EnvType.CLIENT)
public class ItemUsage extends Component implements IMinecraft {
   public static final ItemUsage INSTANCE = new ItemUsage();
   public boolean useItem;
   public boolean releaseItem = true;

   @EventInit
   public void onPacket(EventPacket e) {
      Packet var10000 = e.getPacket();
      Objects.requireNonNull(var10000);
      Object var2 = var10000;
      switch (var2) {
         case PlayerActionC2SPacket player when player.getAction().equals(Action.RELEASE_USE_ITEM):
            this.releaseItem = true;
            break;
         case ClientStatusC2SPacket status when status.getMode().equals(Mode.PERFORM_RESPAWN):
            this.releaseItem = true;
            break;
         case PlayerRespawnS2CPacket respawn:
            this.releaseItem = true;
            break;
         case GameJoinS2CPacket join:
            this.releaseItem = true;
            break;
         default:
      }
   }

   public void useHand(Hand hand) {
      if (this.releaseItem) {
         mc.interactionManager.interactItem(mc.player, hand);
         this.releaseItem = false;
      }

      this.useItem = true;
   }

   @Generated
   public void setUseItem(boolean useItem) {
      this.useItem = useItem;
   }

   @Generated
   public void setReleaseItem(boolean releaseItem) {
      this.releaseItem = releaseItem;
   }

   @Generated
   public boolean isUseItem() {
      return this.useItem;
   }

   @Generated
   public boolean isReleaseItem() {
      return this.releaseItem;
   }
}
