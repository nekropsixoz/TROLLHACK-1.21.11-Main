package lol.ethane.mixin.accessor;

import com.mojang.brigadier.suggestion.Suggestion;
import java.util.List;
import net.minecraft.class_4717.class_464;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_464.class})
public interface SuggestionsListAccessor {
   @Accessor("field_25709")
   List<Suggestion> getSuggestionList();

   @Accessor("field_2766")
   int getCurrent();
}
