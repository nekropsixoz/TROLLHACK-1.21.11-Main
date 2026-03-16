package ru.noxium.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import ru.noxium.Noxium;
import ru.noxium.cfg.Config;
import ru.noxium.cfg.ConfigManager;
import ru.noxium.commands.Command;
import ru.noxium.commands.CommandContext;
import ru.noxium.commands.CommandException;
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.Colors;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public final class ConfigCommand implements Command {
   private static final ConfigCommand INSTANCE = new ConfigCommand();
   private static final List<String> COMMAND_ALIASES = List.of(".cfg");
   private static final Map<String, ConfigCommand.CommandMetadata> COMMAND_DEFINITIONS;
   private static final List<String> SUB_COMMANDS;
   private static final String SUPPORTED_COMMANDS = "save/load/list/delete";

   private ConfigCommand() {
   }

   public static ConfigCommand getInstance() {
      return INSTANCE;
   }

   public List<String> getCommandAliases() {
      return COMMAND_ALIASES;
   }

   public List<String> getSubCommands() {
      return SUB_COMMANDS;
   }

   public Map<String, ConfigCommand.CommandMetadata> getCommandMetadata() {
      return COMMAND_DEFINITIONS;
   }

   @Override
   public String name() {
      return "config";
   }

   @Override
   public List<String> aliases() {
      return COMMAND_ALIASES;
   }

   @Override
   public String usage() {
      return COMMAND_ALIASES.get(0) + " <save/load/list/delete>";
   }

   @Override
   public String description() {
      return "Manage client configuration profiles";
   }

   @Override
   public void execute(CommandContext context, String arguments) throws CommandException {
      if (Noxium.get.configManager == null) {
         throw new CommandException("Configuration system is not ready yet");
      } else if (arguments != null && !arguments.isBlank()) {
         String[] parts = arguments.split("\\s+", 2);
         String subCommand = parts[0].toLowerCase(Locale.ROOT);
         String remainder = parts.length > 1 ? parts[1].trim() : "";
         switch (subCommand) {
            case "save":
               this.handleSave(context, remainder);
               break;
            case "load":
               this.handleLoad(context, remainder);
               break;
            case "list":
               this.handleList(context);
               break;
            case "delete":
               this.handleDelete(context, remainder);
               break;
            default:
               throw new CommandException("Unknown command. Use save/load/list/delete");
         }
      } else {
         context.sendInfo(
               "Usage: "
                     + COMMAND_DEFINITIONS.values().stream()
                           .map(metadata -> COMMAND_ALIASES.get(0) + " " + metadata.usage())
                           .collect(Collectors.joining(", ")));
      }
   }

   private void handleSave(CommandContext context, String name) throws CommandException {
      if (name != null && !name.isBlank()) {
         ConfigManager configManager = Noxium.get.configManager;
         if (configManager.saveConfig(name)) {
            Noxium.get.manager.get(Hud.class).showNotification("cfg", "РЎРѕС…СЂР°РЅРµРЅ РєРѕРЅС„РёРі " + name, 6000L,
                  Renderer2D.ColorUtil.getTextTwoColor(1, 1));
            context.sendSuccess("Config '" + name + "' saved");
         } else {
            throw new CommandException("Failed to save config '" + name + "'");
         }
      } else {
         throw new CommandException("Specify config name");
      }
   }

   private void handleLoad(CommandContext context, String name) throws CommandException {
      if (name != null && !name.isBlank()) {
         ConfigManager configManager = Noxium.get.configManager;
         if (configManager.loadConfig(name)) {
            Noxium.get.manager.get(Hud.class).showNotification("cfg", "Р—Р°РіСЂСѓР¶РµРЅ РєРѕРЅС„РёРі " + name, 6000L,
                  Renderer2D.ColorUtil.getTextTwoColor(1, 1));
            context.sendSuccess("Config '" + name + "' loaded");
         } else {
            throw new CommandException("Config '" + name + "' not found or failed to load");
         }
      } else {
         throw new CommandException("Specify config name to load");
      }
   }

   private void handleList(CommandContext context) {
      ConfigManager configManager = Noxium.get.configManager;
      List<Config> configs = configManager.getContents();
      if (configs.isEmpty()) {
         context.sendInfo("No available configs");
      } else {
         MutableText builder = Text.literal("Available configs: ");

         for (int i = 0; i < configs.size(); i++) {
            String name = configs.get(i).getName();
            MutableText nameText = Text.literal(name);
            nameText = nameText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.getClientPrimary())));
            builder = builder.append(nameText);
            if (i < configs.size() - 1) {
               builder = builder.append(Text.literal(" | "));
            }
         }

         context.sendInfo(builder);
      }
   }

   private void handleDelete(CommandContext context, String name) throws CommandException {
      if (name != null && !name.isBlank()) {
         ConfigManager configManager = Noxium.get.configManager;
         if (configManager.deleteConfig(name)) {
            Noxium.get.manager.get(Hud.class).showNotification("cfg", "РЈРґР°Р»РµРЅ РєРѕРЅС„РёРі " + name, 6000L,
                  Renderer2D.ColorUtil.getTextTwoColor(1, 1));
            context.sendSuccess("Config '" + name + "' deleted");
         } else {
            throw new CommandException("Config '" + name + "' not found or failed to delete");
         }
      } else {
         throw new CommandException("Specify config name to delete");
      }
   }

   static {
      Map<String, ConfigCommand.CommandMetadata> commands = new LinkedHashMap<>();
      commands.put("save", new ConfigCommand.CommandMetadata("save <name>", ConfigCommand.ArgumentType.NEW_CONFIG_NAME,
            "Save current config"));
      commands.put(
            "load", new ConfigCommand.CommandMetadata("load <name>", ConfigCommand.ArgumentType.EXISTING_CONFIG_NAME,
                  "Load a config and apply its settings"));
      commands.put("list",
            new ConfigCommand.CommandMetadata("list", ConfigCommand.ArgumentType.NONE, "Show all available configs"));
      commands.put("delete", new ConfigCommand.CommandMetadata("delete <name>",
            ConfigCommand.ArgumentType.EXISTING_CONFIG_NAME, "Delete a saved config"));
      COMMAND_DEFINITIONS = Collections.unmodifiableMap(commands);
      SUB_COMMANDS = List.copyOf(COMMAND_DEFINITIONS.keySet());
   }

   @Environment(EnvType.CLIENT)
   public static enum ArgumentType {
      NONE,
      NEW_CONFIG_NAME,
      EXISTING_CONFIG_NAME;
   }

   @Environment(EnvType.CLIENT)
   public record CommandMetadata(String usage, ConfigCommand.ArgumentType argumentType, String description) {
   }
}
