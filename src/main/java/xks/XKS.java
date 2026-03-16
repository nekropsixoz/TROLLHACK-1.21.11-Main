package xks;

import xks.go.XKS__N__LD;

public class XKS {
    public static String use = "System integrity verification";

    public static String decrypt(String strToDecrypt) {
        try {
            return XKS__N__LD.d_d_d_123(strToDecrypt);
        } catch (UnsatisfiedLinkError e) {
            System.exit(0);
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}