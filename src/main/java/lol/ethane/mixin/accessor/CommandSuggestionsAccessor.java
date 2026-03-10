package lol.ethane.mixin.accessor;

import net.minecraft.class_4717;
import net.minecraft.class_4717.class_464;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4717.class})
public interface CommandSuggestionsAccessor {
   @Accessor("field_21612")
   class_464 getSuggestions();
}
