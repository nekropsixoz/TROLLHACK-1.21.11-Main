package lol.ethane.utils.render.animation;

import java.util.function.Function;
import lombok.Generated;

public enum Easing {
   SMOOTH((x) -> {
      return -2.0D * Math.pow(x, 3.0D) + 3.0D * Math.pow(x, 2.0D);
   }),
   DECELERATE((x) -> {
      return 1.0D - (x - 1.0D) * (x - 1.0D);
   }),
   IN_OUT_SINE((x) -> {
      return -(Math.cos(3.141592653589793D * x) - 1.0D) / 2.0D;
   }),
   OUT_SINE((x) -> {
      return Math.sin(x * 1.5707963267948966D);
   }),
   OUT_ELASTIC((x) -> {
      return x == 0.0D ? 0.0D : (x == 1.0D ? 1.0D : Math.pow(2.0D, -10.0D * x) * Math.sin((x * 10.0D - 0.75D) * 2.0943951023931953D) * 0.5D + 1.0D);
   }),
   OUT_BACK((x) -> {
      return 1.0D + 2.70158D * Math.pow(x - 1.0D, 3.0D) + 1.70158D * Math.pow(x - 1.0D, 2.0D);
   }),
   OUT_QUART((x) -> {
      return 1.0D - Math.pow(1.0D - x, 4.0D);
   }),
   OUT_EXPO((x) -> {
      return x == 1.0D ? 1.0D : 1.0D - Math.pow(2.0D, -10.0D * x);
   }),
   IN_BACK((x) -> {
      return 2.70158D * x * x * x - 1.70158D * x * x;
   }),
   IN_SINE((x) -> {
      return Math.sqrt(1.0D - Math.pow(x - 1.0D, 2.0D));
   }),
   LINEAR((x) -> {
      return x;
   }),
   ELASTIC_BOUNCE((t) -> {
      double freq = 2.6D;
      double decay = 4.0D;
      return 1.0D - Math.cos(freq * t * 2.0D * 3.141592653589793D) / Math.exp(decay * t);
   });

   private final Function<Double, Double> function;

   @Generated
   public Function<Double, Double> getFunction() {
      return this.function;
   }

   @Generated
   private Easing(final Function<Double, Double> function) {
      this.function = function;
   }

   // $FF: synthetic method
   private static Easing[] $values() {
      return new Easing[]{SMOOTH, DECELERATE, IN_OUT_SINE, OUT_SINE, OUT_ELASTIC, OUT_BACK, OUT_QUART, OUT_EXPO, IN_BACK, IN_SINE, LINEAR, ELASTIC_BOUNCE};
   }
}
