package lol.ethane.mixin.accessor;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_1297.class)
public interface EntityAccessor {
    @Accessor("field_5957")
    boolean getNoClip();
    
    @Accessor("field_5964")
    it.unimi.dsi.fastutil.objects.Object2DoubleMap getFluidHeight();
    
    @Accessor("field_25599")
    java.util.Set getCollidingBlockPos();
    
    @Invoker("method_18795")
    class_243 invokeGetMovementDirection(class_243 movementInput, float speed, float yaw);
}
