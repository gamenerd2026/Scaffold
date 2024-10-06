package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.BuiltInGameState;
import com.mineplex.studio.sdk.modules.game.MineplexGameModule;
import com.mineplex.studio.scaffold.OneBlockWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StartCommand extends Command {
    private static final String COMMAND_NAME = "start";

    private final MineplexGameModule gameModule;

    public StartCommand(MineplexGameModule gameModule) {
        super(COMMAND_NAME,
                "Start the game.",
                String.format("/%s", COMMAND_NAME), List.of());
        this.gameModule = gameModule;
    }

    @Override
    public boolean execute(
            @NotNull final CommandSender commandSender, @NotNull final String s, @NotNull final String[] args) {
        gameModule.getCurrentGame().ifPresent(currentGame -> {
            currentGame.setGameState(BuiltInGameState.PRE_START);
            if(currentGame instanceof OneBlockWars oneBlockWars) {
                oneBlockWars.tryStart(true);
            }
        });
        return true;
    }
}