package lol.ethane.mixin.accessor;

import net.minecraft.class_342;
import net.minecraft.class_408;
import net.minecraft.class_4717;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_408.class)
public interface ChatScreenAccessor {
    @Accessor("field_2382")
    class_342 getChatField();
    
    @Accessor("field_21616")
    class_4717 getCommandSuggestions();
}
