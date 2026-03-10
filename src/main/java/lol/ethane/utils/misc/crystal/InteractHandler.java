package lol.ethane.utils.misc.crystal;

import java.util.concurrent.atomic.DoubleAdder;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1511;
import net.minecraft.class_1799;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3966;
import net.minecraft.class_5134;
import net.minecraft.class_5712;
import net.minecraft.class_746;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2824.class_5908;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

public class InteractHandler implements class_5908 {
   @Unique
   private final class_310 client;
   @Unique
   private final DoubleAdder damageAdder = new DoubleAdder();

   public InteractHandler(class_310 client) {
      this.client = client;
   }

   public void method_34219(@NotNull class_1268 interactionHand) {
   }

   public void method_34220(@NotNull class_1268 interactionHand, @NotNull class_243 vec3) {
   }

   public void method_34218() {
      class_239 hitResult = this.client.field_1765;
      if (hitResult instanceof class_3966) {
         class_3966 entityHitResult = (class_3966)hitResult;
         class_1297 entity = entityHitResult.method_17782();
         if (entity instanceof class_1511) {
            class_1511 crystal = (class_1511)entity;
            class_746 player = this.client.field_1724;
            if (player != null) {
               if (this.canDestroyCrystal(player)) {
                  this.destroyCrystal(crystal);
               }

            }
         }
      }
   }

   private boolean canDestroyCrystal(class_746 player) {
      class_1293 weakness = player.method_6112(class_1294.field_5911);
      if (weakness == null) {
         return true;
      } else {
         double baseDamage = player.method_45325(class_5134.field_23721);
         double weaknessPenalty = 4.0D * (double)(weakness.method_5578() + 1);
         if (baseDamage > weaknessPenalty + 5.0D) {
            return true;
         } else {
            return this.calculateTotalDamage(player) > 0.0D;
         }
      }
   }

   private double calculateTotalDamage(class_746 player) {
      double baseDamage = player.method_45325(class_5134.field_23721);
      double weaponDamage = this.getWeaponDamage(player.method_6047());
      class_1293 strength = player.method_6112(class_1294.field_5910);
      double strengthBonus = strength != null ? 3.0D * (double)(strength.method_5578() + 1) : 0.0D;
      class_1293 weakness = player.method_6112(class_1294.field_5911);
      double weaknessPenalty = weakness != null ? 4.0D * (double)(weakness.method_5578() + 1) : 0.0D;
      return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
   }

   private double getWeaponDamage(class_1799 item) {
      if (item.method_7960()) {
         return 0.0D;
      } else {
         this.damageAdder.reset();
         item.method_57354(class_1304.field_6173, (attribute, modifier) -> {
            if (class_5134.field_23721.equals(attribute)) {
               this.damageAdder.add(modifier.comp_2449());
            }

         });
         return this.damageAdder.sum();
      }
   }

   private void destroyCrystal(class_1297 crystal) {
      crystal.method_5650(class_5529.field_26998);
      crystal.method_32876(class_5712.field_37676);
   }
}
