package lol.ethane.mixin.input;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import lol.ethane.feature.command.repository.CommandRepository;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_4717;
import net.minecraft.class_637;
import net.minecraft.class_4717.class_464;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4717.class})
public abstract class CommandSuggestionsMixin {
   @Shadow
   @Nullable
   private ParseResults<class_637> field_21610;
   @Shadow
   @Final
   class_310 field_21597;
   @Shadow
   @Final
   class_342 field_21599;
   @Shadow
   private class_464 field_21612;
   @Shadow
   boolean field_21614;
   @Shadow
   @Nullable
   private CompletableFuture<Suggestions> field_21611;

   @Shadow
   protected abstract void method_23937();

   @Inject(
      method = {"method_23934"},
      at = {@At(
   value = "INVOKE",
   target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
   remap = false
)},
      cancellable = true
   )
   private void onRefresh(CallbackInfo ci) {
      String prefix = "]";
      String text = this.field_21599.method_1882();
      if (text.startsWith(prefix)) {
         StringReader reader = new StringReader(text);
         reader.setCursor(prefix.length());
         if (this.field_21610 == null) {
            this.field_21610 = CommandRepository.DISPATCHER.parse(reader, this.field_21597.method_1562().method_2875());
         }

         int cursor = this.field_21599.method_1881();
         if (cursor >= 1 && (this.field_21612 == null || !this.field_21614)) {
            this.field_21611 = CommandRepository.DISPATCHER.getCompletionSuggestions(this.field_21610, cursor);
            this.field_21611.thenRun(() -> {
               if (this.field_21611.isDone()) {
                  this.method_23937();
               }

            });
         }

         ci.cancel();
      }

   }
}
