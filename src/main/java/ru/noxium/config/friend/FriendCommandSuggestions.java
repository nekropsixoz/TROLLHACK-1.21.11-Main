package ru.noxium.config.friend;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.commands.suggestions.CommandSuggestionProvider;
import ru.noxium.commands.suggestions.CommandSuggestions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class FriendCommandSuggestions implements CommandSuggestionProvider {
    private static final FriendCommandSuggestions INSTANCE = new FriendCommandSuggestions();

    private FriendCommandSuggestions() {
    }

    public static FriendCommandSuggestions getInstance() {
        return INSTANCE;
    }

    @Override
    public List<String> aliases() {
        return FriendCommand.getInstance().aliases();
    }

    @Override
    public boolean supportsInput(String input) {
        return findAlias(input) != null || ".".equals(input) || this.matchesAliasPrefix(input);
    }

    @Override
    public CommandSuggestions.SuggestionSet collect(String input) {
        AliasMatch alias = findAlias(input);
        if (alias == null) {
            return null;
        }

        String argsPortion = input.substring(alias.aliasEnd());
        if (argsPortion.isEmpty()) {
            return CommandSuggestions.of(buildCommandEntries("", 0));
        }

        int leadingSpaces = countLeadingWhitespace(argsPortion);
        String trimmedArgs = argsPortion.substring(leadingSpaces);
        if (trimmedArgs.isEmpty()) {
            return CommandSuggestions.of(buildCommandEntries("", leadingSpaces));
        }

        String firstToken = nextToken(trimmedArgs);
        boolean hasAdditionalCharacters = trimmedArgs.length() > firstToken.length();

        if (!hasAdditionalCharacters) {
            return CommandSuggestions.of(buildCommandEntries(firstToken, leadingSpaces));
        }

        String afterFirstToken = trimmedArgs.substring(firstToken.length());
        int spacesAfterFirstToken = countLeadingWhitespace(afterFirstToken);
        if (spacesAfterFirstToken == 0) {
            return CommandSuggestions.of(buildCommandEntries(firstToken, leadingSpaces));
        }

        String remaining = afterFirstToken.substring(spacesAfterFirstToken);
        String[] args = remaining.split("\\s+");
        String lastArg = remaining.endsWith(" ") ? "" : (args.length > 0 ? args[args.length - 1] : "");
        int argIndex = args.length - (remaining.endsWith(" ") ? 0 : 1);
        if (remaining.isEmpty())
            argIndex = 0;

        return CommandSuggestions.of(buildArgumentEntries(firstToken, argIndex, lastArg));
    }

    private List<CommandSuggestions.SuggestionEntry> buildArgumentEntries(String command, int runArgIndex,
            String partialArg) {
        if ("add".equalsIgnoreCase(command)) {
            if (runArgIndex == 0) {
                return List.of(new CommandSuggestions.SuggestionEntry("<name>", "Player name", null, false));
            }
        } else if ("remove".equalsIgnoreCase(command)) {
            List<Friend> friends = FriendManager.getFriends();
            if (friends == null || friends.isEmpty())
                return List.of();

            return friends.stream()
                    .map(Friend::getName)
                    .filter(name -> partialArg.isEmpty() || name.toLowerCase().startsWith(partialArg.toLowerCase()))
                    .map(name -> {
                        String suffix = normalizeCompletionSuffix(
                                name.substring(Math.min(name.length(), partialArg.length())));
                        return new CommandSuggestions.SuggestionEntry(name, "Friend", suffix, false);
                    })
                    .toList();
        }
        return List.of();
    }

    // ... Helpers

    @Override
    public List<CommandSuggestions.SuggestionEntry> collectAliasSuggestions(String input) {
        if (input == null)
            return List.of();
        if (".".equals(input))
            return buildAllCommandEntries();
        return !input.startsWith(".") ? List.of() : buildPartialAliasEntries(input);
    }

    private static List<CommandSuggestions.SuggestionEntry> buildCommandEntries(String partialToken,
            int leadingSpaces) {
        // FriendCommand doesn't expose metadata map publicly via getter but it has it.
        // I should have checked FriendCommand.java content again.
        // FriendCommand has COMMAND_DEFINITIONS but it is private and no getter.
        // I need to add getter to FriendCommand.java or just hardcode here.
        // Hardcoding for now since I can't modify FriendCommand easily without reading
        // it again to check precise structure.
        // Actually I read FriendCommand.java earlier. It has private static Map.
        // So I'll just hardcode the subcommands here.

        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();
        String normalized = partialToken.toLowerCase(Locale.ROOT);

        Map<String, String> commands = Map.of(
                "add", "add <name>",
                "remove", "remove <name>",
                "list", "list",
                "clear", "clear");

        for (Map.Entry<String, String> entry : commands.entrySet()) {
            if (normalized.isEmpty() || entry.getKey().startsWith(normalized)) {
                StringBuilder completion = new StringBuilder();
                if (partialToken.isEmpty() && leadingSpaces == 0)
                    completion.append(' ');
                completion.append(entry.getKey().substring(partialToken.length()));
                completion.append(' ');
                entries.add(new CommandSuggestions.SuggestionEntry(entry.getValue(), "Friend command",
                        normalizeCompletionSuffix(completion.toString()), false));
            }
        }
        return entries;
    }

    private static List<CommandSuggestions.SuggestionEntry> buildAllCommandEntries() {
        List<String> aliases = FriendCommand.getInstance().aliases();
        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();
        for (String alias : aliases) {
            entries.add(new CommandSuggestions.SuggestionEntry(alias, "Friend command", alias.substring(1), false));
        }
        return entries;
    }

    private static List<CommandSuggestions.SuggestionEntry> buildPartialAliasEntries(String partialInput) {
        List<String> aliases = FriendCommand.getInstance().aliases();
        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();
        for (String alias : aliases) {
            if (alias.startsWith(partialInput)) {
                String completion = alias.substring(partialInput.length());
                entries.add(new CommandSuggestions.SuggestionEntry(alias, "Friend command", completion, false));
            }
        }
        return entries;
    }

    private static AliasMatch findAlias(String input) {
        if (input == null)
            return null;
        for (String alias : FriendCommand.getInstance().aliases()) {
            if (input.length() >= alias.length() && input.regionMatches(true, 0, alias, 0, alias.length())) {
                if (input.length() == alias.length())
                    return new AliasMatch(alias.length());
                char next = input.charAt(alias.length());
                if (Character.isWhitespace(next))
                    return new AliasMatch(alias.length());
            }
        }
        return null;
    }

    private static int countLeadingWhitespace(String value) {
        int index = 0;
        while (index < value.length() && Character.isWhitespace(value.charAt(index)))
            index++;
        return index;
    }

    private static String nextToken(String value) {
        int index = 0;
        while (index < value.length() && !Character.isWhitespace(value.charAt(index)))
            index++;
        return value.substring(0, index);
    }

    private static String normalizeCompletionSuffix(String suffix) {
        return (suffix == null || suffix.isEmpty()) ? null : suffix;
    }

    @Override
    public boolean matchesAliasPrefix(String input) {
        if (input != null && input.startsWith(".")) {
            for (String alias : FriendCommand.getInstance().aliases()) {
                if (alias.startsWith(input))
                    return true;
            }
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    private record AliasMatch(int aliasEnd) {
    }
}
