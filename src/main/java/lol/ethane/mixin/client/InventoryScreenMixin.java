package lol.ethane.mixin.client;

import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_490;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_490.class})
public class InventoryScreenMixin {
   @Inject(
      method = {"method_2486"},
      at = {@At("HEAD")}
   )
   private static void hookDrawEntityHead(class_332 guiGraphics, int i, int j, int k, int l, int m, float f, float g, float h, class_1309 livingEntity, CallbackInfo ci) {
      if (class_310.method_1551().field_1724 != null && livingEntity == class_310.method_1551().field_1724) {
         RotationHelper.getClientHandler().setTicking(true);
      }

   }

   @Inject(
      method = {"method_2486"},
      at = {@At("TAIL")}
   )
   private static void hookDrawEntityTail(class_332 guiGraphics, int i, int j, int k, int l, int m, float f, float g, float h, class_1309 livingEntity, CallbackInfo ci) {
      if (class_310.method_1551().field_1724 != null && livingEntity == class_310.method_1551().field_1724) {
         RotationHelper.getClientHandler().setTicking(false);
      }

   }
}
