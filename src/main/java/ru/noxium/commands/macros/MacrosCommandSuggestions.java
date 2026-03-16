package ru.noxium.commands.macros;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.commands.suggestions.CommandSuggestionProvider;
import ru.noxium.commands.suggestions.CommandSuggestions;
import ru.noxium.util.keyboard.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MacrosCommandSuggestions implements CommandSuggestionProvider {
    private static final MacrosCommandSuggestions INSTANCE = new MacrosCommandSuggestions();

    private MacrosCommandSuggestions() {
    }

    public static MacrosCommandSuggestions getInstance() {
        return INSTANCE;
    }

    @Override
    public List<String> aliases() {
        return MacrosCommand.getInstance().aliases();
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

        // Arguments handling
        String afterFirstToken = trimmedArgs.substring(firstToken.length());
        int spacesAfterFirstToken = countLeadingWhitespace(afterFirstToken);
        if (spacesAfterFirstToken == 0) {
            return CommandSuggestions.of(buildCommandEntries(firstToken, leadingSpaces));
        }

        String remaining = afterFirstToken.substring(spacesAfterFirstToken);
        // firstToken is the subcommand (e.g. "add", "remove")
        // remaining is the arguments string (e.g. "spawn ...")

        // Simple argument handling: splitting by spaces
        String[] args = remaining.split("\\s+");
        String lastArg = args.length > 0 ? args[args.length - 1] : "";
        boolean isNewArg = remaining.endsWith(" ");
        String currentArg = isNewArg ? "" : lastArg;

        // currentArg is what we are typing now.
        // But we need to know WHICH argument it is.
        // logic:
        // if remaining is empty -> arg1 (e.g. name), partial is ""
        // if user typed "add ", remaining is "", arg1
        // if user typed "add spa", remaining "spa", arg1
        // if user typed "add spawn ", remaining "spawn ", user about to type arg2

        int argIndex = args.length - (isNewArg ? 0 : 1);
        if (remaining.isEmpty())
            argIndex = 0;

        return CommandSuggestions.of(buildArgumentEntries(firstToken, argIndex, currentArg));
    }

    private List<CommandSuggestions.SuggestionEntry> buildArgumentEntries(String command, int runArgIndex,
            String partialArg) {
        if ("add".equalsIgnoreCase(command)) {
            if (runArgIndex == 0) {
                return List.of(new CommandSuggestions.SuggestionEntry("<name>", "Macro name", null, false));
            } else if (runArgIndex == 1) {
                return List.of(new CommandSuggestions.SuggestionEntry("<command>", "Command to execute", null, false));
            } else {
                return List.of(new CommandSuggestions.SuggestionEntry("<key>", "Keybind", null, false));
            }
        } else if ("remove".equalsIgnoreCase(command)) {
            if (runArgIndex == 0) {
                // Suggest existing macros
                return Noxium.get.macroManager.getMacros().stream()
                        .map(Macro::getName)
                        .filter(name -> name.toLowerCase().startsWith(partialArg.toLowerCase()))
                        .map(name -> new CommandSuggestions.SuggestionEntry(name, "Existing macro",
                                normalizeCompletionSuffix(name.substring(partialArg.length())), false))
                        .toList();
            }
        }
        return List.of();
    }

    @Override
    public List<CommandSuggestions.SuggestionEntry> collectAliasSuggestions(String input) {
        if (input == null)
            return List.of();
        if (".".equals(input))
            return buildAllCommandEntries();
        return !input.startsWith(".") ? List.of() : buildPartialAliasEntries(input);
    }

    // ... Helpers (copied/adapted from ConfigCommandSuggestions) ...
    private static List<CommandSuggestions.SuggestionEntry> buildCommandEntries(String partialToken,
            int leadingSpaces) {
        String normalized = partialToken.toLowerCase(Locale.ROOT);
        Map<String, MacrosCommand.CommandMetadata> metadataMap = MacrosCommand.getInstance().getCommandMetadata();
        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();

        for (Map.Entry<String, MacrosCommand.CommandMetadata> entry : metadataMap.entrySet()) {
            String command = entry.getKey();
            if (normalized.isEmpty() || command.startsWith(normalized)) {
                // Create suggestion
                StringBuilder completion = new StringBuilder();
                if (partialToken.isEmpty() && leadingSpaces == 0)
                    completion.append(' ');
                completion.append(command.substring(partialToken.length()));
                // Just append space unconditionally for commands that take args
                completion.append(' ');

                entries.add(new CommandSuggestions.SuggestionEntry(entry.getValue().usage(),
                        entry.getValue().description(), normalizeCompletionSuffix(completion.toString()), false));
            }
        }
        return entries;
    }

    private static List<CommandSuggestions.SuggestionEntry> buildAllCommandEntries() {
        List<String> aliases = MacrosCommand.getInstance().aliases();
        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();
        for (String alias : aliases) {
            entries.add(new CommandSuggestions.SuggestionEntry(alias, "Macros command", alias.substring(1), false));
        }
        return entries;
    }

    private static List<CommandSuggestions.SuggestionEntry> buildPartialAliasEntries(String partialInput) {
        List<String> aliases = MacrosCommand.getInstance().aliases();
        List<CommandSuggestions.SuggestionEntry> entries = new ArrayList<>();
        for (String alias : aliases) {
            if (alias.startsWith(partialInput)) {
                String completion = alias.substring(partialInput.length());
                entries.add(new CommandSuggestions.SuggestionEntry(alias, "Macros command", completion, false));
            }
        }
        return entries;
    }

    private static AliasMatch findAlias(String input) {
        if (input == null)
            return null;
        for (String alias : MacrosCommand.getInstance().aliases()) {
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
            for (String alias : MacrosCommand.getInstance().aliases()) {
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
