package lol.ethane.feature.scripting.wrapper.impl;

import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaWorldWrapper extends DynamicLuaWrapper {
   public LuaWorldWrapper() {
      this.registerMethod("get_block", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaWorldWrapper.this.getStart(args);
            int x = args.checkint(start);
            int y = args.checkint(start + 1);
            int z = args.checkint(start + 2);
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.NIL;
            } else {
               class_2338 pos = new class_2338(x, y, z);
               class_2680 state = class_310.method_1551().field_1687.method_8320(pos);
               return LuaValue.valueOf(state.method_26204().method_9518().getString());
            }
         }
      });
      this.registerMethod("is_air", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaWorldWrapper.this.getStart(args);
            int x = args.checkint(start);
            int y = args.checkint(start + 1);
            int z = args.checkint(start + 2);
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.FALSE;
            } else {
               class_2338 pos = new class_2338(x, y, z);
               return LuaValue.valueOf(class_310.method_1551().field_1687.method_8320(pos).method_26215());
            }
         }
      });
      this.registerMethod("get_entities", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.NIL;
            } else {
               LuaTable table = new LuaTable();
               int index = 1;
               Iterator var4 = class_310.method_1551().field_1687.method_18112().iterator();

               while(var4.hasNext()) {
                  class_1297 entity = (class_1297)var4.next();
                  table.set(index++, new LuaEntityWrapper(entity));
               }

               return table;
            }
         }
      });
      this.registerMethod("raycast", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaWorldWrapper.this.getStart(args);
            double maxDistance = args.checkdouble(start);
            if (class_310.method_1551().field_1724 == null) {
               return LuaValue.NIL;
            } else {
               class_1297 camera = class_310.method_1551().method_1560();
               if (camera == null) {
                  return LuaValue.NIL;
               } else {
                  class_243 eyePos = camera.method_5836(1.0F);
                  class_243 viewVec = camera.method_5828(1.0F);
                  class_243 reachVec = eyePos.method_1031(viewVec.field_1352 * maxDistance, viewVec.field_1351 * maxDistance, viewVec.field_1350 * maxDistance);
                  class_238 aabb = camera.method_5829().method_18804(viewVec.method_1021(maxDistance)).method_1009(1.0D, 1.0D, 1.0D);
                  class_3966 hitResult = class_1675.method_18075(camera, eyePos, reachVec, aabb, (e) -> {
                     return !e.method_7325() && e.method_5863();
                  }, maxDistance * maxDistance);
                  return (LuaValue)(hitResult != null ? new LuaEntityWrapper(hitResult.method_17782()) : LuaValue.NIL);
               }
            }
         }
      });
      this.registerMethod("raycast_block", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaWorldWrapper.this.getStart(args);
            double maxDistance = args.checkdouble(start);
            if (class_310.method_1551().field_1724 == null) {
               return LuaValue.NIL;
            } else {
               class_239 hitResult = class_310.method_1551().field_1724.method_5745(maxDistance, 1.0F, false);
               if (hitResult.method_17783() == class_240.field_1332) {
                  class_3965 blockHit = (class_3965)hitResult;
                  class_2338 pos = blockHit.method_17777();
                  LuaTable table = new LuaTable();
                  table.set("x", LuaValue.valueOf(pos.method_10263()));
                  table.set("y", LuaValue.valueOf(pos.method_10264()));
                  table.set("z", LuaValue.valueOf(pos.method_10260()));
                  table.set("block_name", LuaValue.valueOf(class_310.method_1551().field_1687.method_8320(pos).method_26204().method_9518().getString()));
                  return table;
               } else {
                  return LuaValue.NIL;
               }
            }
         }
      });
      this.registerMethod("get_player_list", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.NIL;
            } else {
               LuaTable table = new LuaTable();
               int index = 1;
               Iterator var4 = class_310.method_1551().field_1687.method_18456().iterator();

               while(var4.hasNext()) {
                  class_1657 player = (class_1657)var4.next();
                  table.set(index++, new LuaEntityWrapper(player));
               }

               return table;
            }
         }
      });
      this.registerMethod("get_dimension", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.NIL;
            } else {
               String dim = class_310.method_1551().field_1687.method_27983().method_29177().method_12832();
               return LuaValue.valueOf(dim);
            }
         }
      });
      this.registerMethod("get_time", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return class_310.method_1551().field_1687 == null ? LuaValue.ZERO : LuaValue.valueOf((double)class_310.method_1551().field_1687.method_8532());
         }
      });
      this.registerMethod("get_weather", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1687 == null) {
               return LuaValue.valueOf("clear");
            } else if (class_310.method_1551().field_1687.method_8546()) {
               return LuaValue.valueOf("thunder");
            } else {
               return class_310.method_1551().field_1687.method_8419() ? LuaValue.valueOf("rain") : LuaValue.valueOf("clear");
            }
         }
      });
   }

   private int getStart(Varargs args) {
      return args.arg(1) instanceof LuaWorldWrapper ? 2 : 1;
   }
}
