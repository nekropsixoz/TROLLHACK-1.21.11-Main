package ru.noxium.commands.macros;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Macro {
    private final String name;
    private final String command;
    private final int key;

    public Macro(String name, String command, int key) {
        this.name = name;
        this.command = command;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public int getKey() {
        return key;
    }
}
