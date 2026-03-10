package lol.ethane.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketBuilder {
   private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
   private final DataOutputStream dos;

   public PacketBuilder() {
      this.dos = new DataOutputStream(this.baos);
   }

   public PacketBuilder writeLong(long v) {
      try {
         this.dos.writeLong(v);
         return this;
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   public PacketBuilder writeInt(int v) {
      try {
         this.dos.writeInt(v);
         return this;
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public PacketBuilder writeByte(int v) {
      try {
         this.dos.writeByte(v);
         return this;
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public PacketBuilder writeBytes(byte[] v) {
      try {
         this.dos.write(v);
         return this;
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public PacketBuilder writeString(String s) {
      byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
      byte[] lenBytes = ByteBuffer.allocate(4).putInt(bytes.length).array();
      this.writeBytes(lenBytes);
      this.writeBytes(bytes);
      return this;
   }

   public byte[] build() {
      return this.baos.toByteArray();
   }
}
