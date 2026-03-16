package ru.noxium.event.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.Event;

@Environment(EnvType.CLIENT)
public class SwingDurationEvent extends Event {
   float animation;

   public float getAnimation() {
      return this.animation;
   }

   public float setAnimation(float an) {
      return this.animation = an;
   }
}
