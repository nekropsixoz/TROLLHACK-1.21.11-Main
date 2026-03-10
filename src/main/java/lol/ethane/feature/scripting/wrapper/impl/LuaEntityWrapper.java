package lol.ethane.feature.scripting.wrapper.impl;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaEntityWrapper extends DynamicLuaWrapper {
   private final class_1297 entity;

   public LuaEntityWrapper(class_1297 entity) {
      this.entity = entity;
      this.registerMethod("get_name", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(entity.method_5477().getString());
         }
      });
      this.register("name", () -> LuaValue.valueOf(entity.method_5477().getString()));
      this.register("id", () -> LuaValue.valueOf(entity.method_5628()));
      this.register("x", () -> LuaValue.valueOf(entity.method_23317()));
      this.register("y", () -> LuaValue.valueOf(entity.method_23318()));
      this.register("z", () -> LuaValue.valueOf(entity.method_23321()));
      this.registerMethod("get_pos", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            LuaTable table = new LuaTable();
            table.set("x", LuaValue.valueOf(entity.method_23317()));
            table.set("y", LuaValue.valueOf(entity.method_23318()));
            table.set("z", LuaValue.valueOf(entity.method_23321()));
            return table;
         }
      });
      this.registerMethod("get_type", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(entity.method_5864().method_5897().getString());
         }
      });
      this.registerMethod("get_id", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(entity.method_5628());
         }
      });
      this.registerMethod("get_health", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (entity instanceof class_1309) {
               class_1309 livingEntity = (class_1309)entity;
               return LuaValue.valueOf((double)livingEntity.method_6032());
            } else {
               return LuaValue.ZERO;
            }
         }
      });
      this.register("health", () -> entity instanceof class_1309 ? LuaValue.valueOf((double)((class_1309)entity).method_6032()) : LuaValue.ZERO);
      this.registerMethod("get_max_health", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (entity instanceof class_1309) {
               class_1309 livingEntity = (class_1309)entity;
               return LuaValue.valueOf((double)livingEntity.method_6063());
            } else {
               return LuaValue.ZERO;
            }
         }
      });
      this.register("max_health", () -> entity instanceof class_1309 ? LuaValue.valueOf((double)((class_1309)entity).method_6063()) : LuaValue.valueOf(20.0));
      this.registerMethod("is_player", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(entity instanceof class_1657);
         }
      });
      this.registerMethod("get_distance", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = args.arg(1) instanceof LuaEntityWrapper ? 2 : 1;
            double x = args.checkdouble(start);
            double y = args.checkdouble(start + 1);
            double z = args.checkdouble(start + 2);
            return LuaValue.valueOf(Math.sqrt(entity.method_5649(x, y, z)));
         }
      });
   }

   public class_1297 getEntity() {
      return this.entity;
   }
}
