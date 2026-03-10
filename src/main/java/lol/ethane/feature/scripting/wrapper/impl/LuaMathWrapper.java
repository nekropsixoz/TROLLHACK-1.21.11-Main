package lol.ethane.feature.scripting.wrapper.impl;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class LuaMathWrapper extends DynamicLuaWrapper {
   public LuaMathWrapper() {
      this.registerMethod("distance", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            double x1 = args.checkdouble(1);
            double y1 = args.checkdouble(2);
            double z1 = args.checkdouble(3);
            double x2 = args.checkdouble(4);
            double y2 = args.checkdouble(5);
            double z2 = args.checkdouble(6);
            double dx = x1 - x2;
            double dy = y1 - y2;
            double dz = z1 - z2;
            return LuaValue.valueOf(Math.sqrt(dx * dx + dy * dy + dz * dz));
         }
      });
      this.registerMethod("angle_to", new VarArgFunction() {
         public LuaValue invoke(Varargs args) {
            double x1 = args.checkdouble(1);
            double y1 = args.checkdouble(2);
            double z1 = args.checkdouble(3);
            double x2 = args.checkdouble(4);
            double y2 = args.checkdouble(5);
            double z2 = args.checkdouble(6);
            double diffX = x2 - x1;
            double diffY = y2 - y1;
            double diffZ = z2 - z1;
            double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
            float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D));
            LuaTable table = new LuaTable();
            table.set("yaw", LuaValue.valueOf((double)yaw));
            table.set("pitch", LuaValue.valueOf((double)pitch));
            return table;
         }
      });
   }
}
