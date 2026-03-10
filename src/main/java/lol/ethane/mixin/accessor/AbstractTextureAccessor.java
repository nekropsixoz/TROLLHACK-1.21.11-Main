package lol.ethane.mixin.accessor;

import net.minecraft.class_1044;
import net.minecraft.class_12137;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1044.class})
public interface AbstractTextureAccessor {
   @Accessor("field_63613")
   void setSampler(class_12137 var1);
}
