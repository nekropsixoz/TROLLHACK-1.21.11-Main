package lol.ethane.mixin.accessor;

import net.minecraft.class_342;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_342.class)
public interface TextFieldWidgetAccessor {
    @Accessor("field_2102")
    int getCursor();
    
    @Accessor("field_2101")
    int getSelectionStart();
}
