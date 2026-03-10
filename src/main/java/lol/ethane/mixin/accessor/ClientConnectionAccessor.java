package lol.ethane.mixin.accessor;

import net.minecraft.class_2535;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2535.class)
public interface ClientConnectionAccessor {
    @Accessor("field_11651")
    io.netty.channel.Channel getChannel();
}
