package lol.ethane.mixin.render;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import lol.ethane.utils.injection.HumanoidRenderStateExtension;
import lol.ethane.utils.render.BlockUtil;
import net.minecraft.class_10426;
import net.minecraft.class_10444;
import net.minecraft.class_11659;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_3881;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_989;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_989.class})
public abstract class ItemInHandLayerMixin<S extends class_10426, M extends class_583<S> & class_3881<?>> extends class_3887<S, M> {
   public ItemInHandLayerMixin(class_3883<S, M> renderLayerParent) {
      super(renderLayerParent);
   }

   @Inject(
      method = {"method_4192"},
      at = {@At("HEAD")}
   )
   private void setThirdPersonStackRef(S armedEntityRenderState, class_10444 itemStackRenderState, class_1799 itemStack, class_1306 humanoidArm, class_4587 poseStack, class_11659 submitNodeCollector, int i, CallbackInfo ci, @Share("itemStack") LocalRef<class_1799> stackRef) {
      if (BlockUtil.isThirdPersonBlockingState(armedEntityRenderState)) {
         stackRef.set(((HumanoidRenderStateExtension)armedEntityRenderState).ethane$getEntity().method_61420(humanoidArm));
      }

   }
}
