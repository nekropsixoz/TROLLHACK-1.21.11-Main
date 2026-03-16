package ru.noxium.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.noxium.Noxium;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(Noxium.get.root, "proxy.json");

    public static ProxyData data = new ProxyData();

    public static void load() {
        if (!FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(FILE)) {
            data = GSON.fromJson(reader, ProxyData.class);
            if (data == null)
                data = new ProxyData();
        } catch (Exception e) {
            e.printStackTrace();
            data = new ProxyData();
        }
    }

    public static void save() {
        try {
            if (!FILE.getParentFile().exists())
                FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Proxy getProxy() {
        if (!data.enabled || data.host.isEmpty())
            return Proxy.NO_PROXY;
        Proxy.Type type = data.type.equalsIgnoreCase("HTTP") ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
        try {
            return new Proxy(type, new InetSocketAddress(data.host, data.port));
        } catch (Exception e) {
            return Proxy.NO_PROXY;
        }
    }

    public static class ProxyData {
        public String type = "SOCKS 5";
        public String host = "";
        public int port = 1080;
        public String username = "";
        public String password = "";
        public boolean enabled = false;
    }
}
