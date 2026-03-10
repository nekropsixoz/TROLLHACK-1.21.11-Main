package lol.ethane.network;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class CryptHandler {
   private final ChaCha20Poly1305 cipher;
   private final SecureRandom random;
   private byte[] derivedKey;

   public CryptHandler(byte[] key) {
      Blake3Digest digest = new Blake3Digest(256);
      byte[] context = "Ethane KDF v1".getBytes(StandardCharsets.UTF_8);
      digest.init(Blake3Parameters.context(context));
      digest.update(key, 0, key.length);
      byte[] derivedKey = new byte[32];
      digest.doFinal(derivedKey, 0);
      this.cipher = new ChaCha20Poly1305();
      this.cipher.init(true, new KeyParameter(derivedKey));
      this.random = new SecureRandom();
   }

   public CryptHandler(byte[] key, boolean v2) {
      Blake3Digest digest = new Blake3Digest(256);
      byte[] context = "Ethane KDF v1".getBytes(StandardCharsets.UTF_8);
      digest.init(Blake3Parameters.context(context));
      digest.update(key, 0, key.length);
      this.derivedKey = new byte[32];
      digest.doFinal(this.derivedKey, 0);
      this.cipher = new ChaCha20Poly1305();
      this.random = new SecureRandom();
   }

   public byte[][] encrypt(byte[] plaintext) throws Exception {
      byte[] nonce = new byte[12];
      this.random.nextBytes(nonce);
      this.cipher.init(true, new ParametersWithIV(new KeyParameter(this.derivedKey), nonce));
      byte[] ciphertext = new byte[this.cipher.getOutputSize(plaintext.length)];
      int len = this.cipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
      this.cipher.doFinal(ciphertext, len);
      return new byte[][]{ciphertext, nonce};
   }

   public byte[] decrypt(byte[] nonce, byte[] ciphertext) throws Exception {
      if (nonce.length != 12) {
         throw new IllegalArgumentException("Invalid nonce length");
      } else {
         this.cipher.init(false, new ParametersWithIV(new KeyParameter(this.derivedKey), nonce));
         byte[] plaintext = new byte[this.cipher.getOutputSize(ciphertext.length)];
         int len = this.cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext, 0);
         this.cipher.doFinal(plaintext, len);
         return Arrays.copyOf(plaintext, plaintext.length);
      }
   }
}
