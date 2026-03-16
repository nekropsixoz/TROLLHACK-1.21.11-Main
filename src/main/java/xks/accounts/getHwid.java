package xks.accounts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class getHwid {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static boolean enabled = false; //   
    private static final String HWID_FILE = "hwid.txt"; //    HWID

    //     (/)
    public static void setEnabled(boolean isEnabled) {
        enabled = isEnabled;
    }

    public static String getHwid() {
        if (!enabled) {
            return null; //  ,  null
        }

        try {
            MessageDigest hash = MessageDigest.getInstance("MD5");
            String data = System.getProperty("os.name") +
                    System.getProperty("os.arch") +
                    System.getProperty("os.version") +
                    Runtime.getRuntime().availableProcessors() +
                    (System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "") +
                    (System.getenv("PROCESSOR_ARCHITECTURE") != null ? System.getenv("PROCESSOR_ARCHITECTURE") : "") +
                    (System.getenv("PROCESSOR_ARCHITEW6432") != null ? System.getenv("PROCESSOR_ARCHITEW6432") : "") +
                    (System.getenv("NUMBER_OF_PROCESSORS") != null ? System.getenv("NUMBER_OF_PROCESSORS") : "");

            byte[] hashedData = hash.digest(data.getBytes("UTF-8"));
            String hwid = bytesToHex(hashedData);

            //  HWID  
            saveHwidToFile(hwid);

            return hwid;
        } catch (Exception e) {
            throw new RuntimeException("   .", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    //    HWID  
    private static void saveHwidToFile(String hwid) {
        try {
            //  ,    
            if (!Files.exists(Paths.get(HWID_FILE))) {
                new File(HWID_FILE).createNewFile();
                System.out.println("Tryna to sozdat FILE");
            }

            //  HWID  
            try (FileWriter writer = new FileWriter(HWID_FILE)) {
                writer.write(hwid);
                System.out.println("Tryna to sozdat WRITER");
            }
        } catch (IOException e) {
            System.err.println("   HWID  : " + e.getMessage());
        }
    }
}