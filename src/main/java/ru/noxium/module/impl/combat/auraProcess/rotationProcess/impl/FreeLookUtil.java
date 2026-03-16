package ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.player.EventLook;
import ru.noxium.event.player.EventRotation;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.Component;
import ru.noxium.util.render.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FreeLookUtil extends Component {
   public static boolean active;
   public static float freeYaw;
   public static float freePitch;

   @EventInit
   public void onEvent(EventLook event) {
      if (active) {
         this.rotateTowards(event.getYaw(), event.getPitch());
         event.cancel();
      }
   }

   @EventInit
   public void onEvent(EventRotation event) {
      if (active) {
         event.setYaw(freeYaw);
         event.setPitch(freePitch);
      } else {
         freeYaw = event.getYaw();
         freePitch = event.getPitch();
      }
   }

   private void rotateTowards(double targetYaw, double targetPitch) {
      freePitch = MathHelper.clamp((float)(freePitch + targetPitch * 0.15), -90.0F, 90.0F);
      freeYaw = (float)(freeYaw + targetYaw * 0.15);
   }
}
