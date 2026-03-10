package lol.ethane.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import net.minecraft.class_637;

public abstract class Command {
   private final String name;
   private final List<String> aliases;

   protected Command(String name, String... args) {
      this.name = name;
      this.aliases = List.of(args);
   }

   protected Command(String name) {
      this.name = name;
      this.aliases = List.of();
   }

   protected abstract void onCommand(LiteralArgumentBuilder<class_637> var1);

   public void registerTo(CommandDispatcher<class_637> dispatcher) {
      this.register(dispatcher, this.name);
      Iterator var2 = this.aliases.iterator();

      while(var2.hasNext()) {
         String alias = (String)var2.next();
         this.register(dispatcher, alias);
      }

   }

   public void register(CommandDispatcher<class_637> dispatcher, String name) {
      LiteralArgumentBuilder<class_637> builder = LiteralArgumentBuilder.literal(name);
      this.onCommand(builder);
      dispatcher.register(builder);
   }

   protected static <T> RequiredArgumentBuilder<class_637, T> argument(String name, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   protected static LiteralArgumentBuilder<class_637> literal(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public List<String> getAliases() {
      return this.aliases;
   }
}
