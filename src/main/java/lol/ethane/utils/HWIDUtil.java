package lol.ethane.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

public class HWIDUtil {
   public static String getHWID() {
      try {
         String os = System.getProperty("os.name").toLowerCase();
         String main = "";
         if (os.contains("win")) {
            main = getWindowsIdentifier();
         } else if (os.contains("mac")) {
            main = getMacIdentifier();
         } else if (os.contains("linux")) {
            main = getLinuxIdentifier();
         }

         if (main == null || main.isEmpty()) {
            main = "UNKNOWN-HWID";
         }

         return bytesToHex(MessageDigest.getInstance("SHA-256").digest(main.getBytes(StandardCharsets.UTF_8)));
      } catch (Exception var2) {
         return "ERROR-HWID";
      }
   }

   private static String getWindowsIdentifier() {
      String result = "";

      String val;
      try {
         Process p = (new ProcessBuilder(new String[]{"wmic", "baseboard", "get", "serialnumber"})).start();
         BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

         String line;
         while((line = input.readLine()) != null) {
            val = line.trim();
            if (!val.isEmpty() && !val.toLowerCase().contains("serialnumber") && !val.toLowerCase().equals("none")) {
               result = val;
               break;
            }
         }

         input.close();
         if (result.isEmpty()) {
            p = (new ProcessBuilder(new String[]{"wmic", "csproduct", "get", "uuid"})).start();
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while((line = input.readLine()) != null) {
               val = line.trim();
               if (!val.isEmpty() && !val.toLowerCase().contains("uuid") && !val.toLowerCase().equals("none") && !val.contains("FFFFFFFF")) {
                  result = val;
                  break;
               }
            }

            input.close();
         }
      } catch (Exception var7) {
         if (result.isEmpty()) {
            try {
               Process p = (new ProcessBuilder(new String[]{"reg", "query", "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid"})).start();
               BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

               while((val = input.readLine()) != null) {
                  if (val.contains("MachineGuid")) {
                     String[] parts = val.split("REG_SZ");
                     if (parts.length > 1) {
                        result = parts[parts.length - 1].trim();
                        break;
                     }
                  }
               }

               input.close();
            } catch (IOException var6) {
               throw new RuntimeException(var6);
            }
         }

         var7.printStackTrace();
      }

      return result.trim();
   }

   private static String getMacIdentifier() {
      String result = "";

      try {
         Process p = Runtime.getRuntime().exec("ioreg -l");
         BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

         String line;
         while((line = input.readLine()) != null) {
            if (line.contains("IOPlatformSerialNumber")) {
               result = line.split("=")[1].trim().replace("\"", "");
               break;
            }
         }

         input.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return result;
   }

   private static String getLinuxIdentifier() {
      String result = "";

      try {
         File file = new File("/sys/class/dmi/id/product_uuid");
         Scanner sc;
         if (file.exists()) {
            sc = new Scanner(file);
            result = sc.next();
            sc.close();
         } else {
            file = new File("/etc/machine-id");
            if (file.exists()) {
               sc = new Scanner(file);
               result = sc.next();
               sc.close();
            }
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return result.trim();
   }

   private static String bytesToHex(byte[] hash) {
      StringBuilder hexString = new StringBuilder(2 * hash.length);

      for(int i = 0; i < hash.length; ++i) {
         String hex = Integer.toHexString(255 & hash[i]);
         if (hex.length() == 1) {
            hexString.append('0');
         }

         hexString.append(hex);
      }

      return hexString.toString();
   }
}
