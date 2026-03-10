package lol.ethane.feature.command.defined.module;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lol.ethane.feature.command.Command;
import lol.ethane.feature.command.arguments.ModuleArgumentType;
import lol.ethane.feature.module.Module;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_637;

public class ToggleCommand extends Command {
   public ToggleCommand() {
      super("toggle");
   }

   protected void onCommand(LiteralArgumentBuilder<class_637> builder) {
      ((LiteralArgumentBuilder)builder.executes((context) -> {
         ChatUtil.sendMessage("Usage: .toggle <module>");
         return 1;
      })).then(argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         module.setEnabled(!module.isEnabled());
         String var10000 = module.getName();
         ChatUtil.sendMessage(var10000 + " is now " + (module.isEnabled() ? "enabled" : "disabled"));
         return 1;
      }));
   }
}
