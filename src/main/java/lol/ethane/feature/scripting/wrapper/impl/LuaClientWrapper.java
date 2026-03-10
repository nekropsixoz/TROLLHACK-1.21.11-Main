package lol.ethane.feature.scripting.wrapper.impl;

import lol.ethane.feature.helper.impl.player.timer.TimerHelper;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_465;
import net.minecraft.class_490;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.lwjgl.glfw.GLFW;

public class LuaClientWrapper extends DynamicLuaWrapper {
   public LuaClientWrapper() {
      this.registerMethod("print", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaClientWrapper) {
               start = 2;
            }

            String message = args.checkjstring(start);
            ChatUtil.sendMessage(message);
            return LuaValue.NIL;
         }
      });
      this.registerMethod("message", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaClientWrapper) {
               start = 2;
            }

            String message = args.checkjstring(start);
            if (class_310.method_1551().field_1724 != null) {
               class_310.method_1551().field_1724.field_3944.method_45729(message);
            }

            return LuaValue.NIL;
         }
      });
      this.registerMethod("set_timer_speed", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaClientWrapper) {
               start = 2;
            }

            double speed = args.checkdouble(start);
            TimerHelper.getInstance().set((float)speed);
            return LuaValue.NIL;
         }
      });
      this.registerMethod("is_key_down", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaClientWrapper) {
               start = 2;
            }

            int key = args.checkint(start);
            long window = class_310.method_1551().method_22683().method_4490();
            boolean isDown = GLFW.glfwGetKey(window, key) == 1;
            return LuaValue.valueOf(isDown);
         }
      });
      this.registerMethod("is_mouse_down", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaClientWrapper) {
               start = 2;
            }

            int button = args.checkint(start);
            long window = class_310.method_1551().method_22683().method_4490();
            boolean isDown = GLFW.glfwGetMouseButton(window, button) == 1;
            return LuaValue.valueOf(isDown);
         }
      });
      this.registerMethod("fps", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(class_310.method_1551().method_47599());
         }
      });
      this.registerMethod("time", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf((double)System.currentTimeMillis());
         }
      });
      this.registerMethod("current_screen", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1755 == null) {
               return LuaValue.valueOf("NONE");
            } else if (class_310.method_1551().field_1755 instanceof class_408) {
               return LuaValue.valueOf("CHAT");
            } else if (class_310.method_1551().field_1755 instanceof class_490) {
               return LuaValue.valueOf("INVENTORY");
            } else {
               return class_310.method_1551().field_1755 instanceof class_465 ? LuaValue.valueOf("CHEST") : LuaValue.valueOf("OTHER");
            }
         }
      });
   }
}
