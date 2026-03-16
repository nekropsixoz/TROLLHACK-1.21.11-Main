package xks.guard.a.b.c.d.e.f.g.h.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.x.anti.leak;

import static xks.guard.a.b.c.d.e.f.g.h.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.x.anti.leak.bypass.NotBypass.*;

public class Bypass {

    String isBypass;
    String isBypass2;
    String isBypass3;

    public static String bypass = "Bypass";

    public Bypass(String isBypass, String isBypass2, String isBypass3) {
        this.isBypass = isBypass;
        this.isBypass2 = isBypass2;
        this.isBypass3 = isBypass3;
    }

    public void makeBypass() {

        if (isBypass != null || isBypass2 != null || isBypass3 != null) {
            if (isBypass == AntiLeak.AntiLeak1) {
                count(bypass);
                {};
                {};
            }
            else {
                XKS__BYPAS__();            {};
                {};

            }

            if (isBypass2 == AntiLeak.AntiLeak2) {
                count(bypass);
                {};
                {};
            }
            else {
                XKS__BYPAS__();
                {};
                {};
            }

            if (isBypass3 == AntiLeak.AntiLeak3) {
                count(bypass);
                {};
                {};
            }
            else {
                XKS__BYPAS__();
                {};
                {};
            }
        }
        else {
            XKS__BYPAS__();
            {};
            {};
        }

    }

    {}
    {}
    {}
    {}
    {};
    {}
    {};
    {}
    {}
    {}
    {};
    {}
    {}
    {}
    {};
    {}


    static void XKS__BYPAS__() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Runtime.getRuntime().halt(0);
            } catch (Exception e) {
                Runtime.getRuntime().halt(1);
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void count(String bypass){
        {}
        {};
        BYPASS111 = BYPASS111_ + BYPASS111__ + BYPASS111___ + bypass;
        if (BYPASS111.equals(BYPASS111____)){
            {}
            {};        {}
            {};        {}       {}
            {};        {}
            {};
        }

        else {
            XKS__BYPAS__();
        }

        {}
    }

}
