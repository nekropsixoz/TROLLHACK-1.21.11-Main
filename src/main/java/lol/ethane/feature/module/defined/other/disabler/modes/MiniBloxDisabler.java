package lol.ethane.feature.module.defined.other.disabler.modes;

import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.module.defined.other.disabler.DisablerModule;
import lol.ethane.feature.module.property.impl.mode.ModuleMode;

public class MiniBloxDisabler extends ModuleMode<DisablerModule> {
   private static final boolean VIA_AVAILABLE;
   
   static {
      boolean available = false;
      try {
         Class.forName("com.viaversion.viafabricplus.ViaFabricPlus");
         available = true;
      } catch (ClassNotFoundException e) {
      }
      VIA_AVAILABLE = available;
   }

   public MiniBloxDisabler(DisablerModule module) {
      super(module);
   }

   @Subscribe
   private void onGameTick(PreGameTickEvent event) {
      if (!VIA_AVAILABLE || this.mc.field_1724 == null) {
         return;
      }
      
      try {
         Object viaImpl = Class.forName("com.viaversion.viafabricplus.ViaFabricPlus")
            .getMethod("getImpl")
            .invoke(null);
         
         Object version = viaImpl.getClass()
            .getMethod("getTargetVersion")
            .invoke(viaImpl);
         
         Class<?> protocolVersionClass = Class.forName("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
         Object v1_8 = protocolVersionClass.getField("v1_8").get(null);
         
         boolean isV1_8 = (boolean) version.getClass()
            .getMethod("equalTo", protocolVersionClass)
            .invoke(version, v1_8);
         
         if (isV1_8) {
            Object connection = viaImpl.getClass()
               .getMethod("getPlayNetworkUserConnection")
               .invoke(viaImpl);
            
            Class<?> packetWrapperClass = Class.forName("com.viaversion.viaversion.api.protocol.packet.PacketWrapper");
            Class<?> serverboundPackets = Class.forName("com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8");
            Object playerInputPacket = serverboundPackets.getField("PLAYER_INPUT").get(null);
            
            Object packet = packetWrapperClass
               .getMethod("create", serverboundPackets, Class.forName("com.viaversion.viaversion.api.connection.UserConnection"))
               .invoke(null, playerInputPacket, connection);
            
            Class<?> typesClass = Class.forName("com.viaversion.viaversion.api.type.Types");
            Object floatType = typesClass.getField("FLOAT").get(null);
            Object byteType = typesClass.getField("BYTE").get(null);
            
            packet.getClass()
               .getMethod("write", Class.forName("com.viaversion.viaversion.api.type.Type"), Object.class)
               .invoke(packet, floatType, this.mc.field_1724.field_3913.method_3128().field_1342);
            
            packet.getClass()
               .getMethod("write", Class.forName("com.viaversion.viaversion.api.type.Type"), Object.class)
               .invoke(packet, floatType, this.mc.field_1724.field_3913.method_3128().field_1343);
            
            byte b = 0;
            if (this.mc.field_1724.field_3913.field_54155.comp_3163()) {
               b = (byte)(b | 1);
            }
            if (this.mc.field_1724.field_3913.field_54155.comp_3164()) {
               b = (byte)(b | 2);
            }
            
            packet.getClass()
               .getMethod("write", Class.forName("com.viaversion.viaversion.api.type.Type"), Object.class)
               .invoke(packet, byteType, b);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Enum<?> getValue() {
      return DisablerModule.Mode.MINI$BLOX;
   }
}
