package lol.ethane.feature.command.defined;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import lol.ethane.Ethane;
import lol.ethane.feature.command.Command;
import lol.ethane.feature.scripting.Script;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_637;

public class ScriptsCommand extends Command {
   public ScriptsCommand() {
      super("scripts");
   }

   protected void onCommand(LiteralArgumentBuilder<class_637> builder) {
      builder.then(literal("list").executes((context) -> {
         Collection<Script> scripts = Ethane.getInstance().getScriptRepository().values();
         if (scripts.isEmpty()) {
            ChatUtil.sendMessage("No scripts loaded.");
            return 1;
         } else {
            ChatUtil.sendMessage("Loaded scripts (" + scripts.size() + "):");
            Iterator var2 = scripts.iterator();

            while(var2.hasNext()) {
               Script script = (Script)var2.next();
               String var10000 = String.valueOf(class_124.field_1080);
               ChatUtil.sendMessage(var10000 + " - " + String.valueOf(class_124.field_1068) + script.getName());
            }

            return 1;
         }
      }));
      builder.then(literal("refresh").executes((context) -> {
         Ethane.getInstance().getScriptRepository().loadScripts();
         ChatUtil.sendMessage("Refreshed scripts directory.");
         return 1;
      }));
      builder.then(literal("reload").then(argument("name", StringArgumentType.word()).suggests((context, b) -> {
         return class_2172.method_9264(Ethane.getInstance().getScriptRepository().values().stream().map(Script::getName), b);
      }).executes((context) -> {
         String name = StringArgumentType.getString(context, "name");
         Script script = Ethane.getInstance().getScriptRepository().get(name);
         if (script == null) {
            ChatUtil.sendErrorMessage("Script not found: " + name);
            return 0;
         } else {
            Ethane.getInstance().getScriptRepository().reloadScript(script);
            ChatUtil.sendMessage("Reloaded script: " + name);
            return 1;
         }
      })));
      builder.then(literal("unload").then(argument("name", StringArgumentType.word()).suggests((context, b) -> {
         return class_2172.method_9264(Ethane.getInstance().getScriptRepository().values().stream().map(Script::getName), b);
      }).executes((context) -> {
         String name = StringArgumentType.getString(context, "name");
         Script script = Ethane.getInstance().getScriptRepository().get(name);
         if (script == null) {
            ChatUtil.sendErrorMessage("Script not found: " + name);
            return 0;
         } else {
            Ethane.getInstance().getScriptRepository().unloadScript(script);
            ChatUtil.sendMessage("Unloaded script: " + name);
            return 1;
         }
      })));
   }
}
