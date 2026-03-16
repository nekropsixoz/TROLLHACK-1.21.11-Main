package xks.guard.a.b.c.d.e.f.g.h.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.x.anti.leak;

public class AntiLeak {
    public static String AntiLeak1 = "AntiLeak";
    public static String AntiLeak2 = "AntiLeak";
    public static String AntiLeak3 = "AntiLeak";
    public static String AntiLeak4 = "AntiLeakAntiLeak";

     public static void a_1() {

         if (AntiLeak1.toLowerCase().equals(AntiLeak2.toLowerCase())) {
             bypass();
         }
         else {
             Bypass.XKS__BYPAS__();
         }
     }


    public static void bypass() {
         Bypass bypass = new Bypass(AntiLeak1,AntiLeak2,AntiLeak3);
         bypass.makeBypass();
     }
}
