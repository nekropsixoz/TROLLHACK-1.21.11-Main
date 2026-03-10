package lol.ethane.network.packets;

import lol.ethane.network.PacketBuilder;

public abstract class Packet {
   protected long timestamp = System.currentTimeMillis();
   private static final byte SOURCE_BASE = 0;

   public abstract byte getPacketId();

   protected byte computePacketSource() {
      long combined = (this.timestamp & 65535L) + 2134547244L;
      return (byte)((int)(0L ^ combined >> 8 & 255L));
   }

   public abstract byte[] serializeBody();

   public byte[] serialize() {
      PacketBuilder pb = new PacketBuilder();
      pb.writeLong(this.timestamp);
      pb.writeByte(this.computePacketSource());
      pb.writeByte(this.getPacketId());
      pb.writeBytes(this.serializeBody());
      return pb.build();
   }
}
