package lol.ethane.mixin.render;

import lol.ethane.Ethane;
import lol.ethane.feature.module.defined.render.ChatModule;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public class ChatComponentMixin {
   @Inject(
      method = {"method_75804"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(class_332 guiGraphics, class_327 font, int i, int j, int k, boolean bl, boolean bl2, CallbackInfo ci) {
      if (((ChatModule)Ethane.getInstance().getModuleRepository().getModule(ChatModule.class)).isEnabled()) {
         ci.cancel();
      }

   }
}
