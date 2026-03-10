package lol.ethane.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import org.lwjgl.glfw.GLFW;

public final class BindArgumentType implements ArgumentType<String> {
   private static final BindArgumentType INSTANCE = new BindArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_BIND = new DynamicCommandExceptionType((name) -> {
      return class_2561.method_43470("No bind with name " + String.valueOf(name) + " exists.");
   });
   private final List<String> binds = new ArrayList();
   private static final Collection<String> EXAMPLES = List.of("A", "B", "C", "D");

   public static BindArgumentType create() {
      return INSTANCE;
   }

   public static String get(CommandContext<?> context) {
      return (String)context.getArgument("bind", String.class);
   }

   private BindArgumentType() {
      Field[] var1 = GLFW.class.getDeclaredFields();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Field field = var1[var3];
         if (field.getName().startsWith("GLFW_KEY_")) {
            this.binds.add(field.getName().substring("GLFW_KEY_".length()));
         }
      }

      for(int i = 0; i < 10; ++i) {
         this.binds.add("MOUSE_" + i);
      }

      this.binds.add("CLEAR");
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      if (!this.binds.contains(argument.toUpperCase())) {
         throw NO_SUCH_BIND.create(argument);
      } else {
         return argument;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9265(this.binds, builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
