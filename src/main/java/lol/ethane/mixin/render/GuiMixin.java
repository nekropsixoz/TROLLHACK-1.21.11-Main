package lol.ethane.mixin.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import lol.aether.MgfxQueueRenderer;
import lol.aether.MgfxRenderer;
import lol.aether.helper.MgfxShaderHelper;
import lol.aether.shader.MgfxContext;
import lol.ethane.Ethane;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.render.Render2DEvent;
import lol.ethane.utils.render.state.GlStates;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public class GuiMixin {
   @Shadow
   @Final
   private class_310 field_2035;

   @Inject(
      method = {"method_1753"},
      at = {@At("TAIL")}
   )
   private void render(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo ci) {
      GlStates.push();
      int width = this.field_2035.method_22683().method_4489();
      int height = this.field_2035.method_22683().method_4506();
      int fbo = MgfxShaderHelper.framebufferId(this.field_2035.method_1522());
      float scale = (float)this.field_2035.method_22683().method_4495();
      MgfxRenderer renderer = Ethane.getInstance().getRenderer();
      if (renderer.getContext() == null) {
         renderer.setContext(new MgfxContext(fbo, width, height, scale));
         renderer.getContext().initRepository();
      } else {
         renderer.getContext().update(fbo, width, height, scale);
      }

      GlStateManager._glBindFramebuffer(36160, renderer.getContext().getFbo());
      EventDispatcher.dispatch(new Render2DEvent(Ethane.getInstance().getRenderer().getContext(), guiGraphics, deltaTracker.method_60637(false)));
      MgfxQueueRenderer.flush();
      renderer.getContext().finishFrame();
      GlStates.pop();
   }

   @Inject(
      method = {"method_1765"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderEffects(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo ci) {
      ci.cancel();
   }
}
