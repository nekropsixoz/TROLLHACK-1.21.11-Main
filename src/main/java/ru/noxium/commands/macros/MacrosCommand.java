package ru.noxium.commands.macros;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import ru.noxium.Noxium;
import ru.noxium.commands.Command;
import ru.noxium.commands.CommandContext;
import ru.noxium.commands.CommandException;
import ru.noxium.ui.Colors;
import ru.noxium.util.keyboard.Keyboard;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class MacrosCommand implements Command {
    private static final MacrosCommand INSTANCE = new MacrosCommand();
    private static final List<String> ALIASES = List.of(".macros", ".macro");
    private static final Map<String, CommandMetadata> COMMAND_DEFINITIONS;

    private MacrosCommand() {
    }

    public static MacrosCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public String name() {
        return "macros";
    }

    @Override
    public List<String> aliases() {
        return ALIASES;
    }

    @Override
    public String usage() {
        return ".macros <add/remove/list/clear>";
    }

    @Override
    public String description() {
        return "Manage macros";
    }

    public List<String> getSubCommands() {
        return List.copyOf(COMMAND_DEFINITIONS.keySet());
    }

    public Map<String, CommandMetadata> getCommandMetadata() {
        return COMMAND_DEFINITIONS;
    }

    @Override
    public void execute(CommandContext context, String arguments) throws CommandException {
        if (arguments != null && !arguments.isBlank()) {
            String[] parts = arguments.split("\\s+", 2);
            String subCommand = parts[0].toLowerCase(Locale.ROOT);
            String remainder = parts.length > 1 ? parts[1].trim() : "";

            switch (subCommand) {
                case "add":
                    handleAdd(context, remainder);
                    break;
                case "remove":
                    handleRemove(context, remainder);
                    break;
                case "list":
                    handleList(context);
                    break;
                case "clear":
                    handleClear(context);
                    break;
                default:
                    throw new CommandException("Unknown command. Use add/remove/list/clear");
            }
        } else {
            context.sendInfo("Usage: .macros <add/remove/list/clear>");
        }
    }

    private void handleAdd(CommandContext context, String args) throws CommandException {
        // args is "spawn /spawn B" or similar
        // We parse by space, assuming name is first, key is last, command is in
        // between.
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 3) {
            throw new CommandException("Usage: .macros add <name> <command> <key>");
        }

        String name = parts[0];
        String keyName = parts[parts.length - 1];

        // Command is everything between name and key
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < parts.length - 1; i++) {
            commandBuilder.append(parts[i]);
            if (i < parts.length - 2) {
                commandBuilder.append(" ");
            }
        }
        String command = commandBuilder.toString();

        int keyCode = Keyboard.keyCode(keyName);
        if (keyCode == -1) {
            throw new CommandException("Invalid key: " + keyName);
        }

        Noxium.get.macroManager.add(name, command, keyCode);
        context.sendSuccess("Macro '" + name + "' added bound to " + keyName.toUpperCase());
    }

    private void handleRemove(CommandContext context, String name) throws CommandException {
        if (name == null || name.isBlank()) {
            throw new CommandException("Specify macro name");
        }

        if (Noxium.get.macroManager.getMacro(name).isEmpty()) {
            throw new CommandException("Macro '" + name + "' not found");
        }

        Noxium.get.macroManager.remove(name);
        context.sendSuccess("Macro '" + name + "' removed");
    }

    private void handleList(CommandContext context) {
        List<ru.noxium.commands.macros.Macro> macros = Noxium.get.macroManager.getMacros();
        if (macros.isEmpty()) {
            context.sendInfo("No macros defined");
        } else {
            MutableText builder = Text.literal("Macros: ");
            for (int i = 0; i < macros.size(); i++) {
                ru.noxium.commands.macros.Macro macro = macros.get(i);
                MutableText text = Text.literal(macro.getName() + " (" + Keyboard.keyName(macro.getKey()) + ")");
                text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.getClientPrimary())));
                builder.append(text);
                if (i < macros.size() - 1) {
                    builder.append(Text.literal(", "));
                }
            }
            context.sendInfo(builder);
        }
    }

    private void handleClear(CommandContext context) {
        Noxium.get.macroManager.clear();
        context.sendSuccess("All macros cleared");
    }

    static {
        Map<String, CommandMetadata> commands = new LinkedHashMap<>();
        commands.put("add", new CommandMetadata("add <name> <command> <key>", "Add a new macro"));
        commands.put("remove", new CommandMetadata("remove <name>", "Remove a macro"));
        commands.put("list", new CommandMetadata("list", "List all macros"));
        commands.put("clear", new CommandMetadata("clear", "Clear all macros"));
        COMMAND_DEFINITIONS = Collections.unmodifiableMap(commands);
    }

    @Environment(EnvType.CLIENT)
    public record CommandMetadata(String usage, String description) {
    }
}
