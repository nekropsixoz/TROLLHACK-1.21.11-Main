package xks.accounts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

public class UuidGetterOld {

    public static void main(String[] args) throws Exception {
        System.out.println("Device UUID: " + getDeviceUUID());
    }

    public static String getDeviceUUID() throws Exception {
        //    
        Map<String, String> systemInfo = new LinkedHashMap<>();
        systemInfo.put("device_name", getHostName());
        systemInfo.put("os", getOsName());
        systemInfo.put("user", getUserName());
        systemInfo.put("cpu", getCpuInfo());
        systemInfo.put("arch", getArch());

        //     
        StringBuilder uniqueData = new StringBuilder();
        for (Map.Entry<String, String> entry : systemInfo.entrySet()) {
            if (uniqueData.length() > 0) {
                uniqueData.append("_");
            }
            uniqueData.append(entry.getValue());
        }

        //  UUID  
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(uniqueData.toString().getBytes(StandardCharsets.UTF_8));
        String hexHash = bytesToHex(hash).substring(0, 32);
/*
        System.out.println("System Info:");
        for (Map.Entry<String, String> entry : systemInfo.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }*/


        return formatAsUUID(hexHash);
    }

    private static String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName().split("\\.")[0];
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static String getOsName() {
        String os = System.getProperty("os.name");
        return os.split(" ")[0];
    }

    private static String getUserName() {
        return System.getProperty("user.name");
    }

    private static String getCpuInfo() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                return System.getenv("PROCESSOR_IDENTIFIER");
            } else if (System.getProperty("os.name").startsWith("Linux")) {
                Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("model name")) {
                            return line.split(":")[1].trim();
                        }
                    }
                }
            } else if (System.getProperty("os.name").startsWith("Mac")) {
                Process process = Runtime.getRuntime().exec("sysctl -n machdep.cpu.brand_string");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    return reader.readLine().trim();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return System.getProperty("os.arch");
    }

    private static String getGpuInfo() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                Process process = Runtime.getRuntime().exec("wmic path win32_VideoController get name");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty() && !line.trim().equals("Name")) {
                            return line.trim();
                        }
                    }
                }
            } else if (System.getProperty("os.name").startsWith("Linux")) {
                Process process = Runtime.getRuntime().exec("lspci -vnnn | grep VGA");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    return reader.readLine().trim();
                }
            } else if (System.getProperty("os.name").startsWith("Mac")) {
                Process process = Runtime.getRuntime().exec("system_profiler SPDisplaysDataType | grep Chipset");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    return reader.readLine().split(":")[1].trim();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "Unknown GPU";
    }

    private static String getArch() {
        return System.getProperty("os.arch");
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String formatAsUUID(String hex) {
        return hex.substring(0, 8) + "-" +
                hex.substring(8, 12) + "-" +
                hex.substring(12, 16) + "-" +
                hex.substring(16, 20) + "-" +
                hex.substring(20, 32);
    }
}