package xks.guard.a.b.c.d.e.f.g.h.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.x.anti.leak.bypass;

import xks.accounts.GetterTwist;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

public class LeakCheck {

    public static void init(Class<?> clazz) {
        try {
            java.lang.annotation.Annotation targetAnnotation = null;
            for (java.lang.annotation.Annotation ann : clazz.getAnnotations()) {
                if (ann.annotationType().getName().endsWith("SysKernelState")) {
                    targetAnnotation = ann;
                    break;
                }
            }

            if (targetAnnotation == null) return;

            Object handler = Proxy.getInvocationHandler(targetAnnotation);

            Field f;
            try {
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException e) {
                f = handler.getClass().getSuperclass().getDeclaredField("memberValues");
            }
            f.setAccessible(true);

            Map<String, Object> memberValues = (Map<String, Object>) f.get(handler);

            memberValues.put("hwid", GetterTwist.getHwid());
            memberValues.put("uid", "ADMIN-SESSION-" + System.nanoTime());
            memberValues.put("status", 1);
            memberValues.put("level", "KERNEL_ELEVATED");

        } catch (Throwable e) {
        }
    }
}