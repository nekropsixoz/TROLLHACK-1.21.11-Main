package lol.ethane.mixin.accessor;

import net.minecraft.class_329;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_329.class)
public interface ChatHudAccessor {
    @Accessor("field_2042")
    int getScrolledLines();
}
