package ru.noxium.rpc.callbacks;

import com.sun.jna.Callback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.rpc.DiscordUser;

@Environment(EnvType.CLIENT)
public interface JoinRequestCallback extends Callback {
   void apply(DiscordUser var1);
}
