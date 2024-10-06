package com.mineplex.studio.scaffold;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandUtil {
    private static final String COMMAND_PREFIX = "obw";

    public static void register(Command command) {
        Bukkit.getCommandMap().register(COMMAND_PREFIX, command);
    }

    public static void unRegister(Command command) {
        CommandMap commandMap = Bukkit.getCommandMap();
        command.unregister(commandMap);
        commandMap.getKnownCommands().remove(command.getName().toLowerCase(Locale.ROOT));
    }
}