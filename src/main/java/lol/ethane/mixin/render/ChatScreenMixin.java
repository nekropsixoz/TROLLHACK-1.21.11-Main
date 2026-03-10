package lol.ethane.mixin.render;

import java.util.Iterator;
import lol.ethane.Ethane;
import lol.ethane.feature.drag.DraggableComponent;
import lol.ethane.feature.module.defined.render.ChatModule;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_408.class})
public abstract class ChatScreenMixin extends class_437 {
   @Shadow
   protected abstract boolean method_75825(class_2583 var1, boolean var2);

   protected ChatScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void render(class_332 guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (((ChatModule)Ethane.getInstance().getModuleRepository().getModule(ChatModule.class)).isEnabled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_25401"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
      ChatModule chatModule = (ChatModule)Ethane.getInstance().getModuleRepository().getModule(ChatModule.class);
      if (chatModule.isEnabled()) {
         chatModule.onScroll(scrollY);
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"method_25402"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(class_11909 mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
      if (mouseButtonEvent.method_74245() == 0) {
         Iterator var4 = Ethane.getInstance().getDraggableRepository().getDraggables().iterator();

         while(var4.hasNext()) {
            DraggableComponent draggable = (DraggableComponent)var4.next();
            if (draggable.isHovered(mouseButtonEvent.comp_4798(), mouseButtonEvent.comp_4799())) {
               draggable.mouseClicked(mouseButtonEvent.comp_4798(), mouseButtonEvent.comp_4799(), mouseButtonEvent.method_74245());
            }
         }
      }

      ChatModule chatModule = (ChatModule)Ethane.getInstance().getModuleRepository().getModule(ChatModule.class);
      if (chatModule.isEnabled()) {
         class_2583 style = chatModule.getStyleAt(mouseButtonEvent.comp_4798(), mouseButtonEvent.comp_4799());
         if (style != null && this.method_75825(style, false)) {
            cir.setReturnValue(true);
         }
      }

   }
}
