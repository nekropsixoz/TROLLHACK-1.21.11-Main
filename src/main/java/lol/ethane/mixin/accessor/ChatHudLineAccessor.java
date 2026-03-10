package lol.ethane.mixin.accessor;

import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_338.class)
public interface ChatHudLineAccessor {
    @Accessor("field_2061")
    java.util.List getMessages();
}
