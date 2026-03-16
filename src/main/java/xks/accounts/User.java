package xks.accounts;

import lombok.Getter;
import xks.util.Role;

import java.util.HashMap;
import java.util.Map;

import static ru.noxium.Auth.getClientVersion;


public class User { //ТУТ НИЧЕГО НЕ ТРОГАТЬ!
    @Getter
    public static String client = "Fantom-1-1-1(DontChangeThis)";
    public static String name = "DefaultPaster";
    public static String uid = "13371488";
    public static String role = "MostSwagaMaster";
    public static String ver = getClientVersion();
    public static String status = " [Stable Version]";

    //     
    private static final Map<String, String> ROLE_TO_TYPE = new HashMap<>();
    static {
        ROLE_TO_TYPE.put("User", "Rework");
        ROLE_TO_TYPE.put("Beta", "Beta");

        //       
    }

    //  ,      
    private static final String DEFAULT_TYPE = "Rework";

    public static String type() {
        return ROLE_TO_TYPE.getOrDefault(role, DEFAULT_TYPE);
    }

    public static String name() {
        return name;
    }
    public static String uid() {
        return uid;
    }
    public static String role() {
        return role;
    }
    public static String ver() {
        return ver;
    }
    public static String status() {
        return status;
    }
    public static Role getRoleObject() {
        return Role.fromString(role);
    }
}