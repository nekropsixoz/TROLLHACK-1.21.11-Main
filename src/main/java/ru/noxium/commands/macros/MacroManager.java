package ru.noxium.commands.macros;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import ru.noxium.event.EventInit;
import ru.noxium.event.EventManager;
import ru.noxium.event.input.KeyInputEvent;
import ru.noxium.util.keyboard.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MacroManager {
    private final List<Macro> macros = new ArrayList<>();

    public void init() {
        EventManager.register(this);
    }

    @EventInit
    public void onKeyInput(KeyInputEvent event) {
        if (event.action() == 1) { // Press
            for (Macro macro : macros) {
                if (macro.getKey() == event.key()) {
                    executeMacro(macro);
                }
            }
        }
    }

    private void executeMacro(Macro macro) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null)
            return;

        String command = macro.getCommand();
        if (command.startsWith("/")) {
            client.getNetworkHandler().sendChatCommand(command.substring(1));
        } else {
            client.getNetworkHandler().sendChatMessage(command);
        }
    }

    public void add(String name, String command, int key) {
        remove(name); // Remove existing if any
        macros.add(new Macro(name, command, key));
    }

    public void remove(String name) {
        macros.removeIf(m -> m.getName().equalsIgnoreCase(name));
    }

    public void clear() {
        macros.clear();
    }

    public List<Macro> getMacros() {
        return Collections.unmodifiableList(macros);
    }

    public Optional<Macro> getMacro(String name) {
        return macros.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
    }
}
