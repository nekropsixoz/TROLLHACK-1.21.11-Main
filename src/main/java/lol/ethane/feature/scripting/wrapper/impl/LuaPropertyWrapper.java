package lol.ethane.feature.scripting.wrapper.impl;

import java.awt.Color;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaPropertyWrapper extends DynamicLuaWrapper {
   private final Property<?> property;

   public LuaPropertyWrapper(Property<?> property) {
      this.property = property;
      this.registerMethod("get", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            Object value = property.getValue();
            if (value instanceof Boolean) {
               return LuaValue.valueOf((Boolean)value);
            } else if (value instanceof Double) {
               return LuaValue.valueOf((Double)value);
            } else if (value instanceof Float) {
               return LuaValue.valueOf((double)(Float)value);
            } else if (value instanceof Integer) {
               return LuaValue.valueOf((Integer)value);
            } else {
               return (LuaValue)(value instanceof Color ? LuaValue.valueOf(((Color)value).getRGB()) : LuaValue.valueOf(String.valueOf(value)));
            }
         }
      });
      this.registerMethod("set", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = 1;
            if (args.arg(1) instanceof LuaPropertyWrapper) {
               start = 2;
            }

            if (property instanceof BooleanProperty) {
               ((BooleanProperty)property).setValue(args.checkboolean(start));
            } else if (property instanceof NumberProperty) {
               ((NumberProperty)property).setValue(args.checkdouble(start));
            } else if (property instanceof ColorProperty) {
               ((ColorProperty)property).setValue(new Color((int)args.checklong(start), true));
            } else if (property instanceof EnumProperty) {
               ((EnumProperty)property).applyValue(args.checkjstring(start));
            }

            return LuaValue.NIL;
         }
      });
   }
}
