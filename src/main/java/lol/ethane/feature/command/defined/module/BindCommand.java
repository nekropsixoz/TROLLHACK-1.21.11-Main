package lol.ethane.feature.command.defined.module;

import com.ibm.icu.impl.Pair;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Optional;
import java.util.Map;
import com.google.common.collect.Multimap;
import lol.ethane.Ethane;
import lol.ethane.feature.binding.IBindable;
import lol.ethane.feature.binding.repository.BindRepository;
import lol.ethane.feature.binding.type.InputType;
import lol.ethane.feature.command.Command;
import lol.ethane.feature.command.arguments.BindArgumentType;
import lol.ethane.feature.command.arguments.ModuleArgumentType;
import lol.ethane.feature.module.Module;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_637;

public class BindCommand extends Command {
   private static final BindRepository BIND_REPOSITORY = Ethane.getInstance().getBindRepository();

   public BindCommand() {
      super("bind");
   }

   protected void onCommand(LiteralArgumentBuilder<class_637> builder) {
      ((LiteralArgumentBuilder)builder.executes((context) -> {
         ChatUtil.sendMessage("Usage: .bind <module> <key|clear> | .bind <list|remove|add>");
         return 1;
      })).then(literal("list").executes((context) -> {
         ChatUtil.sendMessage("§6Current binds:");
         Multimap<Pair<Integer, InputType>, IBindable> bindingMap = BIND_REPOSITORY.getBindingService().getBindingMap();
         if (bindingMap.isEmpty()) {
            ChatUtil.sendMessage("§7No binds set.");
         } else {
            for (Map.Entry<Pair<Integer, InputType>, IBindable> entry : bindingMap.entries()) {
               Pair<Integer, InputType> binding = entry.getKey();
               String keyName = getKeyName(binding.first);
               IBindable bindable = entry.getValue();
               if (bindable instanceof Module) {
                  Module module = (Module) bindable;
                  ChatUtil.sendMessage("§7" + module.getName() + " §f-> §a" + keyName);
               }
            }
         }
         return 1;
      })).then(literal("remove").then(argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         Optional<Pair<Integer, InputType>> bind = BIND_REPOSITORY.getBindingService().getKeyFromBindable(module);
         if (bind.isPresent()) {
            BIND_REPOSITORY.getBindingService().clearBindings(module);
            ChatUtil.sendMessage("§cRemoved bind for " + module.getName());
         } else {
            ChatUtil.sendErrorMessage("§7" + module.getName() + " §fis not bound.");
         }
         return 1;
      }))).then(literal("add").then(argument("module", ModuleArgumentType.create()).then(argument("bind", BindArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         String bind = (String)context.getArgument("bind", String.class);
         String bindName = bind.toUpperCase();
         BIND_REPOSITORY.getBindingService().clearBindings(module);
         Integer bindCode = (Integer)BIND_REPOSITORY.getNamedBindingMap().get(bindName);
         if (bindCode == null) {
            ChatUtil.sendErrorMessage("Unknown bind: " + bindName);
            return 1;
         } else {
            if (bindCode < 10 && bindName.startsWith("MOUSE_")) {
               BIND_REPOSITORY.getBindingService().register(bindCode, module, InputType.MOUSE);
            } else {
               BIND_REPOSITORY.getBindingService().register(bindCode, module, InputType.KEYBOARD);
            }

            ChatUtil.sendMessage("§aBound " + module.getName() + " to " + bindName);
            return 1;
         }
      })))).then(((RequiredArgumentBuilder)argument("module", ModuleArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         Optional<Pair<Integer, InputType>> bind = Ethane.getInstance().getBindRepository().getBindingService().getKeyFromBindable(module);
         if (bind.isPresent()) {
            String var10000 = module.getName();
            ChatUtil.sendMessage(var10000 + " is currently bound to " + String.valueOf(((Pair)bind.get()).first));
         } else {
            ChatUtil.sendMessage(module.getName() + " is not bound.");
         }

         return 1;
      })).then(argument("bind", BindArgumentType.create()).executes((context) -> {
         Module module = (Module)context.getArgument("module", Module.class);
         String bind = (String)context.getArgument("bind", String.class);
         String bindName = bind.toUpperCase();
         BIND_REPOSITORY.getBindingService().clearBindings(module);
         if (bindName.equals("CLEAR")) {
            ChatUtil.sendMessage("Cleared bind for " + module.getName());
            return 1;
         } else {
            Integer bindCode = (Integer)BIND_REPOSITORY.getNamedBindingMap().get(bindName);
            if (bindCode == null) {
               ChatUtil.sendErrorMessage("Unknown bind: " + bindName);
               return 1;
            } else {
               if (bindCode < 10 && bindName.startsWith("MOUSE_")) {
                  BIND_REPOSITORY.getBindingService().register(bindCode, module, InputType.MOUSE);
               } else {
                  BIND_REPOSITORY.getBindingService().register(bindCode, module, InputType.KEYBOARD);
               }

               String var10000 = module.getName();
               ChatUtil.sendMessage("Bound " + var10000 + " to " + bindName);
               return 1;
            }
         }
      })));
   }
   
   private String getKeyName(int keyCode) {
      // Find the key name from the named binding map
      for (Map.Entry<String, Integer> entry : BIND_REPOSITORY.getNamedBindingMap().entrySet()) {
         if (entry.getValue().equals(keyCode)) {
            return entry.getKey();
         }
      }
      return String.valueOf(keyCode);
   }
}
