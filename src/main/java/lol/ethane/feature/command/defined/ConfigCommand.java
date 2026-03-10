package lol.ethane.feature.command.defined;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lol.ethane.Ethane;
import lol.ethane.feature.command.Command;
import lol.ethane.utils.data.SaveUtil;
import lol.ethane.utils.misc.ChatUtil;
import net.minecraft.class_637;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config");
    }

    protected void onCommand(LiteralArgumentBuilder<class_637> builder) {
        ((LiteralArgumentBuilder)builder.executes((context) -> {
            ChatUtil.sendMessage("Usage: .config <save|list|dir|load> [filename]");
            return 1;
        })).then(literal("save").executes((context) -> {
            SaveUtil.saveAll();
            ChatUtil.sendMessage("§aConfiguration saved successfully!");
            return 1;
        }).then(argument("filename", com.mojang.brigadier.arguments.StringArgumentType.string()).executes((context) -> {
            String filename = (String)context.getArgument("filename", String.class);
            if (!filename.endsWith(".json")) {
                filename += ".json";
            }
            try {
                File configFile = new File(SaveUtil.DIRECTORY, filename);
                if (!SaveUtil.DIRECTORY.exists()) {
                    SaveUtil.DIRECTORY.mkdir();
                }
                String configContent = new com.google.gson.GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create()
                    .toJson(Ethane.getInstance().getModuleRepository().getModules());
                Files.writeString(configFile.toPath(), configContent);
                ChatUtil.sendMessage("§aConfiguration saved to " + filename);
            } catch (IOException e) {
                ChatUtil.sendErrorMessage("§cFailed to save configuration: " + e.getMessage());
            }
            return 1;
        }))).then(literal("list").executes((context) -> {
            ChatUtil.sendMessage("§6Available configurations:");
            if (!SaveUtil.DIRECTORY.exists()) {
                ChatUtil.sendMessage("§7Config directory does not exist.");
                return 1;
            }
            
            File[] configFiles = SaveUtil.DIRECTORY.listFiles((dir, name) -> 
                name.endsWith(".json") && !name.equals("bindings.json") && !name.equals("draggables.json"));
            
            if (configFiles == null || configFiles.length == 0) {
                ChatUtil.sendMessage("§7No configuration files found.");
            } else {
                List<String> fileNames = Arrays.stream(configFiles)
                    .map(File::getName)
                    .sorted()
                    .collect(Collectors.toList());
                
                for (String fileName : fileNames) {
                    File file = new File(SaveUtil.DIRECTORY, fileName);
                    long sizeKB = file.length() / 1024;
                    String displayName = fileName.replace(".json", "");
                    if (fileName.equals("config.json")) {
                        ChatUtil.sendMessage("§a" + displayName + " §7(§f" + sizeKB + "KB§7) §8[Current]");
                    } else {
                        ChatUtil.sendMessage("§7" + displayName + " §7(§f" + sizeKB + "KB§7)");
                    }
                }
            }
            return 1;
        })).then(literal("dir").executes((context) -> {
            String configPath = SaveUtil.DIRECTORY.getAbsolutePath();
            ChatUtil.sendMessage("§6Configuration directory: §f" + configPath);
            return 1;
        })).then(literal("load").executes((context) -> {
            SaveUtil.loadConfig();
            ChatUtil.sendMessage("§aConfiguration loaded successfully!");
            return 1;
        }).then(argument("filename", com.mojang.brigadier.arguments.StringArgumentType.string()).executes((context) -> {
            String filename = (String)context.getArgument("filename", String.class);
            if (!filename.endsWith(".json")) {
                filename += ".json";
            }
            
            File configFile = new File(SaveUtil.DIRECTORY, filename);
            if (!configFile.exists()) {
                ChatUtil.sendErrorMessage("§cConfiguration file not found: " + filename);
                return 1;
            }
            
            try {
                SaveUtil.loadConfig(filename);
                ChatUtil.sendMessage("§aConfiguration loaded from " + filename);
            } catch (Exception e) {
                ChatUtil.sendErrorMessage("§cFailed to load configuration: " + e.getMessage());
            }
            return 1;
        })));
    }
}
