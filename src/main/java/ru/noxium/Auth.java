package ru.noxium;

import xks.accounts.Managment;
import xks.accounts.Profile;
import xks.accounts.User;
import xks.go.XKS__N__LD;
import xks.util.forge;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Auth {

    public static final String CLIENT_VERSION = "1.0";

    public static String getClientVersion() {
        return CLIENT_VERSION;
    }

    public static void init() {
        try {
            XKS__N__LD._init_0x();
        } catch (Throwable t) {
/*            System.out.println("Крашу на " +t);
            Logger.info("Крашу на " +t);*/
setName();
        }

        String myIntegrityHash = XKS__N__LD.calculateClientHash();

        String integrityResult = XKS__N__LD.c(myIntegrityHash);

        if (!"OK".equals(integrityResult)) {
/*            System.out.println("1");
            Logger.info("1!");*/
            setName();
        }

        String nativeResult = XKS__N__LD.a("", "");

        String[] parts = nativeResult.split("::::");

        if (parts.length < 7) {
 /*           System.out.println("2");
            Logger.info("2!");*/
            setName();
        }

        boolean isSuccess = "TRUE".equals(parts[0]);
        String serverMessage = parts[1];

        if (!isSuccess) {
     //        System.out.println("Auth Error: " + serverMessage);
           //       System.out.println("HWID: " + XKS__N__LD.b());
/*            System.out.println("3");
            Logger.info("3!");*/
            setName();
        }

        String nickname = parts[2];
        String uid = parts[3];
        String role = parts[4];
        String subscriptionEnd = parts[5];

        if (subscriptionEnd == null || "null".equals(subscriptionEnd) || subscriptionEnd.length() < 10) {
/*            System.out.println("5");
            Logger.info("5!");*/
            setName();
        }

        String subscriptionDateOnly = subscriptionEnd.split(" ")[0];

        try {
            User.name = nickname;
            User.role = role;
            User.uid = uid;
            User.client = "Fantom";
            Managment.USER_PROFILE = new Profile(
                    nickname,
                    Integer.parseInt(uid),
                    role
            );
        } catch (Exception e) {
/*            System.out.println("6");
            Logger.info("6!");*/
            setName();
        }

        if (!"2048-01-01".equals(subscriptionDateOnly)) {
            try {
                LocalDate endDate = LocalDate.parse(subscriptionDateOnly);
                if (endDate.isBefore(LocalDate.now())) {
                    System.out.println("Subscription expired: " + subscriptionDateOnly);
/*                    System.out.println("7");
                    Logger.info("7!");*/
                    setName();
                }
            } catch (Exception e) {
/*                System.out.println("8");
                Logger.info("8!");*/
                setName();
            }
        }


        forge.start(); // скрывает консоль после всего

    }

    public static void setName(){
/*        System.out.println("9");
        Logger.info("9!");*/
       X.sex();
        X.hardCrash();
        X.immediate(0);
        X.destroy(0);
        X.a(0);
        X.terminate(0);
        X.finish(0);
        X.shutdown(0);
        X.stop(0);
        X.end(0);
        X.exit(0);
        X.quit(0);
        X.delayed(0, 1);
        X.graceful(0);
        System.exit(0);
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
