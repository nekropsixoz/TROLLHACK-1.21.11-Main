package lol.ethane.mixin.network;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lol.ethane.feature.command.repository.CommandRepository;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_2535;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_8673;
import net.minecraft.class_8675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public abstract class ClientPacketListenerMixin extends class_8673 {
   protected ClientPacketListenerMixin(class_310 minecraft, class_2535 connection, class_8675 commonListenerCookie) {
      super(minecraft, connection, commonListenerCookie);
   }

   @Inject(
      method = {"method_45729"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendChatMessage(String message, CallbackInfo ci) {
      String trimmedMessage = message.trim();
      if (trimmedMessage.startsWith("]")) {
         try {
            CommandRepository.dispatch(trimmedMessage.substring(1));
         } catch (CommandSyntaxException var5) {
            ChatUtil.sendErrorMessage(var5.getMessage());
         }

         this.field_45588.field_1705.method_1743().method_1803(message);
         ci.cancel();
      }

   }
}
