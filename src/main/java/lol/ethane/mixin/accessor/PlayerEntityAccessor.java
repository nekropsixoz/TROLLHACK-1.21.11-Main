package lol.ethane.mixin.accessor;

import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_1657.class)
public interface PlayerEntityAccessor {
    @Accessor("field_7490")
    boolean getReducedDebugInfo();
}
