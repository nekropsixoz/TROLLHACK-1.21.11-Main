package lol.ethane.mixin.render.humanoid;

import lol.ethane.utils.injection.HumanoidRenderStateExtension;
import net.minecraft.class_10034;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_10034.class})
public class HumanoidRenderStateMixin implements HumanoidRenderStateExtension {
   @Unique
   public class_1309 entity;

   public class_1309 ethane$getEntity() {
      return this.entity;
   }

   public void ethane$setEntity(class_1309 entity) {
      this.entity = entity;
   }
}
