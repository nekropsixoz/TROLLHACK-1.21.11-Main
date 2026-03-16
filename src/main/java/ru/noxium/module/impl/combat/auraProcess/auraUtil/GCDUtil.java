package ru.noxium.module.impl.combat.auraProcess.auraUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.util.other.IMinecraft;

@Environment(EnvType.CLIENT)
public class GCDUtil implements IMinecraft {
   public static float getSensitivity(float rot) {
      return getDeltaMouse(rot) * getGCDValue();
   }

   public static float getGCDValue() {
      return (float)(getGCD() * 0.15);
   }

   public static float getGCD() {
      float f1;
      return (f1 = (float)((Double)mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2)) * f1 * f1 * 8.0F;
   }

   public static float getDeltaMouse(float delta) {
      return Math.round(delta / getGCDValue());
   }
}
