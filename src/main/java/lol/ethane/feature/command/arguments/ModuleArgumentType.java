package lol.ethane.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import lol.ethane.Ethane;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.UnknownModuleException;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class ModuleArgumentType implements ArgumentType<Module> {
   private static final ModuleArgumentType INSTANCE = new ModuleArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_MODULE = new DynamicCommandExceptionType((name) -> {
      return class_2561.method_43470("Module " + String.valueOf(name) + " doesn't exist.");
   });

   public static ModuleArgumentType create() {
      return INSTANCE;
   }

   public static Module get(CommandContext<?> context) {
      return (Module)context.getArgument("module", Module.class);
   }

   public Module parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();

      try {
         return Ethane.getInstance().getModuleRepository().getModule(argument);
      } catch (UnknownModuleException var4) {
         throw NO_SUCH_MODULE.create(argument);
      }
   }

   public Collection<String> getExamples() {
      return Ethane.getInstance().getModuleRepository().getModules().stream().limit(3L).map(Module::getId).toList();
   }

   public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return class_2172.method_9264(Ethane.getInstance().getModuleRepository().getModules().stream().map(Module::getId), builder);
   }
}
