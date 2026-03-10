package lol.ethane.network.packets;

import lol.ethane.network.PacketBuilder;
import lol.ethane.utils.HWIDUtil;

public class ClientAuthPacket extends Packet {
   private final byte[] challengeSolution;
   private final int uid;
   private final byte[] publicKey;
   private final String hwid;

   public ClientAuthPacket(byte[] challengeSolution, int uid, byte[] publicKey) {
      this.challengeSolution = challengeSolution;
      this.uid = uid;
      this.publicKey = publicKey;
      this.hwid = HWIDUtil.getHWID();
   }

   public byte getPacketId() {
      return 2;
   }

   public byte[] serializeBody() {
      PacketBuilder pb = new PacketBuilder();
      pb.writeBytes(this.challengeSolution);
      pb.writeInt(this.uid);
      pb.writeBytes(this.publicKey);
      pb.writeBytes(this.hwid.getBytes());
      return pb.build();
   }
}
