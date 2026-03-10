package lol.ethane.feature.scripting.wrapper.impl;

import java.awt.Color;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.ColorProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.feature.scripting.wrapper.module.LuaModule;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaModuleWrapper extends DynamicLuaWrapper {
   public LuaModuleWrapper(LuaModule module) {
      this.registerMethod("event_callback", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            String eventName = args.checkjstring(start);
            LuaClosure func = args.checkclosure(start + 1);
            module.event_callback(eventName, func);
            return LuaValue.NIL;
         }
      });
      this.registerMethod("on_enable", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            module.event_callback("enable", args.checkclosure(start));
            return LuaValue.NIL;
         }
      });
      this.registerMethod("on_disable", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            module.event_callback("disable", args.checkclosure(start));
            return LuaValue.NIL;
         }
      });
      this.registerMethod("toggle", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            module.toggle();
            return LuaValue.NIL;
         }
      });
      this.registerMethod("add_toggle", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            String name = args.checkjstring(start);
            boolean defaultValue = args.checkboolean(start + 1);
            BooleanProperty property = new BooleanProperty(name, defaultValue);
            module.addProperties(new Property[]{property});
            return new LuaPropertyWrapper(property);
         }
      });
      this.registerMethod("add_slider", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            String name = args.checkjstring(start);
            double defaultValue = args.checkdouble(start + 1);
            double min = args.checkdouble(start + 2);
            double max = args.checkdouble(start + 3);
            double increment = args.checkdouble(start + 4);
            NumberProperty property = new NumberProperty(name, defaultValue, min, max, increment);
            module.addProperties(new Property[]{property});
            return new LuaPropertyWrapper(property);
         }
      });
      this.registerMethod("add_mode", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            String name = args.checkjstring(start);
            String defaultValue = args.checkjstring(start + 1);
            LuaTable optionsTable = args.checktable(start + 2);
            String[] options = new String[optionsTable.length()];

            for(int i = 0; i < options.length; ++i) {
               options[i] = optionsTable.get(i + 1).tojstring();
            }

            EnumProperty<String> property = new EnumProperty(name, defaultValue, options);
            module.addProperties(new Property[]{property});
            return new LuaPropertyWrapper(property);
         }
      });
      this.registerMethod("add_color", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            int start = LuaModuleWrapper.this.getStart(args);
            String name = args.checkjstring(start);
            int defaultValue = (int)args.checklong(start + 1);
            ColorProperty property = new ColorProperty(name, new Color(defaultValue, true));
            module.addProperties(new Property[]{property});
            return new LuaPropertyWrapper(property);
         }
      });
   }

   private int getStart(Varargs args) {
      return args.arg(1) instanceof LuaModuleWrapper ? 2 : 1;
   }
}
