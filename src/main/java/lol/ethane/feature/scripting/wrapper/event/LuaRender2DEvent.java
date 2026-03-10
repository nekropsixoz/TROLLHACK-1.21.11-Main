package lol.ethane.feature.scripting.wrapper.event;

import lol.aether.builders.Rectangle;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.feature.scripting.wrapper.impl.DynamicLuaWrapper;
import net.minecraft.class_310;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaRender2DEvent extends DynamicLuaWrapper {
   private final Render2DEvent event;

   public LuaRender2DEvent(Render2DEvent event) {
      this.event = event;
      this.register("width", () -> LuaValue.valueOf(event.getGraphics().method_51421()));
      this.register("height", () -> LuaValue.valueOf(event.getGraphics().method_51443()));
      this.register("delta", () -> LuaValue.valueOf((double) event.getDelta()));
      this.register("mouse_x", () -> LuaValue.valueOf(getMouseX()));
      this.register("mouse_y", () -> LuaValue.valueOf(getMouseY()));
      this.registerMethod("get_mouse_pos", new VarArgFunction() {
         @Override
         public LuaValue invoke(Varargs args) {
            org.luaj.vm2.LuaTable t = new org.luaj.vm2.LuaTable();
            t.set("x", LuaValue.valueOf(getMouseX()));
            t.set("y", LuaValue.valueOf(getMouseY()));
            return t;
         }
      });
      this.registerMethod("draw_rect", new VarArgFunction() {
         @Override
         public LuaValue invoke(Varargs args) {
            int start = args.narg() >= 5 ? 1 : 1;
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double w = args.checkdouble(start + 2);
            double h = args.checkdouble(start + 3);
            int color = (int) args.checklong(start + 4);
            event.getContext().drawRectangle(
               Rectangle.builder().xywh((float) x, (float) y, (float) w, (float) h).color(color));
            return LuaValue.NIL;
         }
      });
      this.registerMethod("draw_outline", new VarArgFunction() {
         @Override
         public LuaValue invoke(Varargs args) {
            int start = 1;
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double w = args.checkdouble(start + 2);
            double h = args.checkdouble(start + 3);
            int color = (int) args.checklong(start + 4);
            double th = args.optdouble(start + 5, 1.0);
            float t = (float) th;
            event.getContext().drawRectangle(Rectangle.builder().xywh((float) x, (float) y, (float) w, t).color(color));
            event.getContext().drawRectangle(Rectangle.builder().xywh((float) x, (float) (y + h - t), (float) w, t).color(color));
            event.getContext().drawRectangle(Rectangle.builder().xywh((float) x, (float) y, t, (float) h).color(color));
            event.getContext().drawRectangle(Rectangle.builder().xywh((float) (x + w - t), (float) y, t, (float) h).color(color));
            return LuaValue.NIL;
         }
      });
      this.registerMethod("draw_text", new VarArgFunction() {
         @Override
         public LuaValue invoke(Varargs args) {
            int start = 1;
            String text = args.checkjstring(start);
            double x = args.checkdouble(start + 1);
            double y = args.checkdouble(start + 2);
            int color = (int) args.checklong(start + 3);
            boolean shadow = args.optboolean(start + 4, true);
            class_310 mc = class_310.method_1551();
            if (mc.field_1772 != null) {
               event.getGraphics().method_51433(mc.field_1772, text, (int) x, (int) y, color, shadow);
            }
            return LuaValue.NIL;
         }
      });
   }

   private static double getMouseX() {
      class_310 mc = class_310.method_1551();
      if (mc == null || mc.method_22683() == null || mc.field_1729 == null) return 0;
      return mc.field_1729.method_1603() * (double) mc.method_22683().method_4486() / (double) mc.method_22683().method_4480();
   }

   private static double getMouseY() {
      class_310 mc = class_310.method_1551();
      if (mc == null || mc.method_22683() == null || mc.field_1729 == null) return 0;
      return mc.field_1729.method_1604() * (double) mc.method_22683().method_4502() / (double) mc.method_22683().method_4507();
   }
}
