package lol.ethane.feature.command.repository;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import lol.ethane.feature.command.Command;
import net.minecraft.class_310;
import net.minecraft.class_637;

public class CommandRepository {
   public static final CommandDispatcher<class_637> DISPATCHER = new CommandDispatcher();
   public static final List<Command> COMMANDS = new ArrayList();

   private CommandRepository(CommandRepository.Builder builder) {
      Iterator var2 = builder.commands.iterator();

      while(var2.hasNext()) {
         Command command = (Command)var2.next();
         add(command);
      }

      COMMANDS.sort(Comparator.comparing(Command::getName));
   }

   public static void dispatch(String message) throws CommandSyntaxException {
      DISPATCHER.execute(message, class_310.method_1551().method_1562().method_2875());
   }

   public static void add(Command command) {
      COMMANDS.removeIf((existing) -> {
         return existing.getName().equals(command.getName());
      });
      command.registerTo(DISPATCHER);
      COMMANDS.add(command);
   }

   public static CommandRepository.Builder builder() {
      return new CommandRepository.Builder();
   }

   public static final class Builder {
      public final List<Command> commands = new ArrayList();

      public CommandRepository.Builder put(Command... commands) {
         Collections.addAll(this.commands, commands);
         return this;
      }

      public CommandRepository build() {
         return new CommandRepository(this);
      }
   }
}
