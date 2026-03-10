package lol.ethane.feature.scripting.wrapper.impl;

import lol.ethane.utils.math.MovementUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaPlayerWrapper extends DynamicLuaWrapper {
   public LuaPlayerWrapper() {
      this.registerMethod("jump", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 != null) {
               class_310.method_1551().execute(() -> {
                  if (class_310.method_1551().field_1724 != null) {
                     class_310.method_1551().field_1724.method_6043();
                  }

               });
            }

            return LuaValue.NIL;
         }
      });
      this.registerMethod("swing_item", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaPlayerWrapper.this.getStart(args);
            int handIndex = args.optint(start, 0);
            class_1268 hand = class_1268.values()[Math.min(class_1268.values().length - 1, Math.max(0, handIndex))];
            if (class_310.method_1551().field_1724 != null) {
               class_310.method_1551().execute(() -> {
                  if (class_310.method_1551().field_1724 != null) {
                     class_310.method_1551().field_1724.method_6104(hand);
                  }

               });
            }

            return LuaValue.NIL;
         }
      });
      this.registerMethod("get_input", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 == null) {
               return LuaValue.NIL;
            } else {
               LuaTable table = new LuaTable();
               table.set("forward", LuaValue.valueOf((double)class_310.method_1551().field_1724.field_3913.method_3128().field_1342));
               table.set("strafe", LuaValue.valueOf((double)class_310.method_1551().field_1724.field_3913.method_3128().field_1343));
               return table;
            }
         }
      });
      this.registerMethod("is_moving", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(class_310.method_1551().field_1724 != null && MovementUtil.isMoving());
         }
      });
      this.registerMethod("is_on_ground", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(class_310.method_1551().field_1724 != null && class_310.method_1551().field_1724.method_24828());
         }
      });
      this.registerMethod("strafe", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaPlayerWrapper.this.getStart(args);
            double speed = args.checkdouble(start);
            if (class_310.method_1551().field_1724 != null) {
               MovementUtil.strafe(speed);
            }

            return LuaValue.NIL;
         }
      });
      this.registerMethod("id", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return (LuaValue)(class_310.method_1551().field_1724 != null ? LuaValue.valueOf(class_310.method_1551().field_1724.method_5628()) : LuaValue.NIL);
         }
      });
      this.registerMethod("get_pos", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 != null) {
               LuaTable table = new LuaTable();
               table.set("x", LuaValue.valueOf(class_310.method_1551().field_1724.method_23317()));
               table.set("y", LuaValue.valueOf(class_310.method_1551().field_1724.method_23318()));
               table.set("z", LuaValue.valueOf(class_310.method_1551().field_1724.method_23321()));
               return table;
            } else {
               return LuaValue.NIL;
            }
         }
      });
      this.registerMethod("get_rotation", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 != null) {
               LuaTable table = new LuaTable();
               table.set("yaw", LuaValue.valueOf((double)class_310.method_1551().field_1724.method_36454()));
               table.set("pitch", LuaValue.valueOf((double)class_310.method_1551().field_1724.method_36455()));
               return table;
            } else {
               return LuaValue.NIL;
            }
         }
      });
      this.registerMethod("get_health", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return class_310.method_1551().field_1724 != null ? LuaValue.valueOf((double)class_310.method_1551().field_1724.method_6032()) : LuaValue.ZERO;
         }
      });
      this.registerMethod("get_max_health", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return class_310.method_1551().field_1724 != null ? LuaValue.valueOf((double)class_310.method_1551().field_1724.method_6063()) : LuaValue.ZERO;
         }
      });
      this.registerMethod("get_hunger", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return (LuaValue)(class_310.method_1551().field_1724 != null ? LuaValue.valueOf(class_310.method_1551().field_1724.method_7344().method_7586()) : LuaValue.ZERO);
         }
      });
      this.registerMethod("get_armor", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return (LuaValue)(class_310.method_1551().field_1724 != null ? LuaValue.valueOf(class_310.method_1551().field_1724.method_6096()) : LuaValue.ZERO);
         }
      });
      this.registerMethod("get_held_item", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 == null) {
               return LuaValue.NIL;
            } else {
               class_1799 stack = class_310.method_1551().field_1724.method_6047();
               if (stack.method_7960()) {
                  return LuaValue.NIL;
               } else {
                  LuaTable table = new LuaTable();
                  table.set("name", LuaValue.valueOf(stack.method_7964().getString()));
                  table.set("count", LuaValue.valueOf(stack.method_7947()));
                  table.set("durability", LuaValue.valueOf(stack.method_7936() - stack.method_7919()));
                  return table;
               }
            }
         }
      });
      this.registerMethod("get_inventory", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            if (class_310.method_1551().field_1724 == null) {
               return LuaValue.NIL;
            } else {
               LuaTable table = new LuaTable();

               for(int i = 0; i < class_310.method_1551().field_1724.method_31548().method_5439(); ++i) {
                  class_1799 stack = class_310.method_1551().field_1724.method_31548().method_5438(i);
                  if (!stack.method_7960()) {
                     LuaTable itemTable = new LuaTable();
                     itemTable.set("name", LuaValue.valueOf(stack.method_7964().getString()));
                     itemTable.set("count", LuaValue.valueOf(stack.method_7947()));
                     itemTable.set("durability", LuaValue.valueOf(stack.method_7936() - stack.method_7919()));
                     itemTable.set("slot", LuaValue.valueOf(i));
                     table.set(i + 1, itemTable);
                  }
               }

               return table;
            }
         }
      });
      this.registerMethod("get_hotbar_slot", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            return (LuaValue)(class_310.method_1551().field_1724 == null ? LuaValue.ZERO : LuaValue.valueOf(class_310.method_1551().field_1724.method_31548().method_67532()));
         }
      });
   }

   private int getStart(Varargs args) {
      return args.arg(1) instanceof LuaPlayerWrapper ? 2 : 1;
   }
}
