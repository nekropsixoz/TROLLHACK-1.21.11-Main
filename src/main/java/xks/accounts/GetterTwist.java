package xks.accounts;

public class GetterTwist {

    public static String hwid;

    //     
    public static String getHwid() {
        if (hwid == null) {
            try {
                hwid = UuidGetterOld.getDeviceUUID();
            } catch (Exception e) {
                System.err.println("Failed to get HWID: " + e.getMessage());
                hwid = "error-uuid-" + System.currentTimeMillis();
            }
        }
        return hwid;
    }

    public GetterTwist() {
        if (hwid == null) {
            hwid = getHwid();
        }
    }
}