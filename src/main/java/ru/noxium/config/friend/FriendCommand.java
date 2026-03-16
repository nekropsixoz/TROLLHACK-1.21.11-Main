package ru.noxium.config.friend;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
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
import ru.noxium.module.impl.visuals.Hud;
import ru.noxium.ui.Colors;
import ru.noxium.util.render.core.Renderer2D;

@Environment(EnvType.CLIENT)
public final class FriendCommand implements Command {
    private static final FriendCommand INSTANCE = new FriendCommand();
    private static final List<String> COMMAND_ALIASES = List.of(".friend", ".f", ".friends");
    private static final Map<String, FriendCommand.CommandMetadata> COMMAND_DEFINITIONS;

    private FriendCommand() {
    }

    public static FriendCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public String name() {
        return "friend";
    }

    @Override
    public List<String> aliases() {
        return COMMAND_ALIASES;
    }

    @Override
    public String usage() {
        return COMMAND_ALIASES.get(0) + " <add/remove/list/clear>";
    }

    @Override
    public String description() {
        return "Manage friends list";
    }

    @Override
    public void execute(CommandContext context, String arguments) throws CommandException {
        if (arguments != null && !arguments.isBlank()) {
            String[] parts = arguments.split("\\s+", 2);
            String subCommand = parts[0].toLowerCase(Locale.ROOT);
            String remainder = parts.length > 1 ? parts[1].trim() : "";
            switch (subCommand) {
                case "add":
                    this.handleAdd(context, remainder);
                    break;
                case "remove":
                    this.handleRemove(context, remainder);
                    break;
                case "list":
                    this.handleList(context);
                    break;
                case "clear":
                    this.handleClear(context);
                    break;
                default:
                    throw new CommandException("Unknown command. Use add/remove/list/clear");
            }
        } else {
            context.sendInfo(
                    "Usage: "
                            + COMMAND_DEFINITIONS.values().stream()
                                    .map(metadata -> COMMAND_ALIASES.get(0) + " " + metadata.usage())
                                    .collect(Collectors.joining(", ")));
        }
    }

    private void handleAdd(CommandContext context, String name) throws CommandException {
        if (name != null && !name.isBlank()) {
            if (Noxium.get.friendManager.isFriend(name)) {
                throw new CommandException("Player '" + name + "' is already a friend");
            } else {
                Noxium.get.friendManager.add(name);
                Noxium.get.manager.get(Hud.class).showNotification("Friend", "Added friend " + name, 4000L,
                        Renderer2D.ColorUtil.getTextTwoColor(1, 1));
                context.sendSuccess("Added friend '" + name + "'");
            }
        } else {
            throw new CommandException("Specify player name");
        }
    }

    private void handleRemove(CommandContext context, String name) throws CommandException {
        if (name != null && !name.isBlank()) {
            if (!Noxium.get.friendManager.isFriend(name)) {
                throw new CommandException("Player '" + name + "' is not in friends list");
            } else {
                Noxium.get.friendManager.remove(name);
                Noxium.get.manager.get(Hud.class).showNotification("Friend", "Removed friend " + name, 4000L,
                        Renderer2D.ColorUtil.getTextTwoColor(1, 1));
                context.sendSuccess("Removed friend '" + name + "'");
            }
        } else {
            throw new CommandException("Specify player name");
        }
    }

    private void handleList(CommandContext context) {
        List<Friend> friends = FriendManager.getFriends();
        if (friends.isEmpty()) {
            context.sendInfo("Friend list is empty");
        } else {
            MutableText builder = Text.literal("Friends: ");

            for (int i = 0; i < friends.size(); i++) {
                String name = friends.get(i).getName();
                MutableText nameText = Text.literal(name);
                nameText = nameText.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Colors.getClientPrimary())));
                builder = builder.append(nameText);
                if (i < friends.size() - 1) {
                    builder = builder.append(Text.literal(", "));
                }
            }

            context.sendInfo(builder);
        }
    }

    private void handleClear(CommandContext context) {
        Noxium.get.friendManager.clearFriend();
        Noxium.get.manager.get(Hud.class).showNotification("Friend", "Friends list cleared", 4000L,
                Renderer2D.ColorUtil.getTextTwoColor(1, 1));
        context.sendSuccess("Friends list cleared");
    }

    static {
        Map<String, FriendCommand.CommandMetadata> commands = new LinkedHashMap<>();
        commands.put("add", new FriendCommand.CommandMetadata("add <name>", "Add a player to friends"));
        commands.put("remove", new FriendCommand.CommandMetadata("remove <name>", "Remove a player from friends"));
        commands.put("list", new FriendCommand.CommandMetadata("list", "Show friends list"));
        commands.put("clear", new FriendCommand.CommandMetadata("clear", "Clear friends list"));
        COMMAND_DEFINITIONS = Collections.unmodifiableMap(commands);
    }

    @Environment(EnvType.CLIENT)
    public record CommandMetadata(String usage, String description) {
    }
}
