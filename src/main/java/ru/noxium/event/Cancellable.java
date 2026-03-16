package ru.noxium.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Cancellable {
   boolean isCancelled();

   void cancel();
}
