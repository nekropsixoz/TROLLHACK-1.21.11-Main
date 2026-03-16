package ru.noxium.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import ru.noxium.Noxium;
import ru.noxium.event.EventInit;
import ru.noxium.event.input.KeyInputEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.util.other.StopWatchShadow;
import ru.noxium.util.render.utils.SoundUtil;

@IModule(
   name = "Middle Click",
   category = Category.Player,
   description = "Allows you to look through the blocks",
   bind = -1
)
@Environment(EnvType.CLIENT)
public class MiddleClick extends Module {
   public static BooleanSetting pearl = new BooleanSetting("Pearl", true);
   public static BindSettings pearlkey = new BindSettings("Pearl Key", -1).hidden(() -> !pearl.get());
   public static BooleanSetting friend = new BooleanSetting("Friend", true);
   public static BindSettings friendkey = new BindSettings("Friend Key", -1).hidden(() -> !friend.get());
   private final StopWatchShadow swapWatchK = new StopWatchShadow();

   public MiddleClick() {
      this.addSettings(new Setting[]{pearl, pearlkey, friend, friendkey});
   }

   @EventInit
   public void onKey(KeyInputEvent e) {
      if (friend.get() && e.key() == friendkey.get() && this.swapWatchK.hasTimeElapsed(200L)) {
         if (HitAura.target != null) {
            return;
         }

         HitResult hitResult = mc.crosshairTarget;
         if (hitResult == null || hitResult.getType() != Type.ENTITY) {
            return;
         }

         if (!(((EntityHitResult)hitResult).getEntity() instanceof PlayerEntity player)) {
            return;
         }

         String name = player.getName().getString();
         if (!Noxium.get.friendManager.isFriend(name)) {
            Text msg = Text.literal("Friend - ")
               .formatted(Formatting.WHITE)
               .append(Text.literal(name).formatted(Formatting.GREEN))
               .append(Text.literal(" added").formatted(Formatting.GRAY));
            mc.player.sendMessage(msg, false);
            Noxium.get.friendManager.add(name);
            SoundUtil.playSound_wav("add", 0.5F);
         } else {
            Text msg = Text.literal("Friend - ")
               .formatted(Formatting.WHITE)
               .append(Text.literal(name).formatted(Formatting.RED))
               .append(Text.literal(" removed").formatted(Formatting.GRAY));
            mc.player.sendMessage(msg, false);
            Noxium.get.friendManager.remove(name);
            SoundUtil.playSound_wav("remove", 0.5F);
         }

         this.swapWatchK.reset();
      }
   }
}
