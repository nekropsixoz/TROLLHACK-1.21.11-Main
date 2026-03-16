package xks.nt;

import xks.XKS;

public class OriginalSwag {
    public static zComplete zComplete;
    public static String SESSION_KEY = "paste_hhyhj_key_swag_placeholder";
    public static String KEY_V2 = "null";
    public static String KEY_V3 = "null";
    private static String _k = null;
    public static void init(String key) {
        if (_k == null && key != null && key.length() > 5) {
            _k = key;
            SESSION_KEY = key.substring(0, 5) + "TW_PART_" + key.length();
            KEY_V2 = "INITIALIZED_BOOT_";
        }
    }
    public static String get(String encryptedData) {
        if (_k == null) return null;
        return XKS.decrypt(encryptedData);
    }
    public static String decryptData(String encryptedData) {
        return get(encryptedData);
    }
}

class zComplete {
    OriginalSwag originalSwag;
    public zComplete(OriginalSwag originalSwag) {
        this.originalSwag = originalSwag;
    }
    public void use(){
        boolean b = OriginalSwag.KEY_V2 == OriginalSwag.KEY_V3;
    }
}