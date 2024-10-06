package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.MineplexModuleManager;
import com.mineplex.studio.sdk.modules.game.GameCycle;
import com.mineplex.studio.sdk.modules.game.MineplexGame;
import com.mineplex.studio.sdk.modules.game.MineplexGameModule;
import com.mineplex.studio.sdk.modules.lobby.LobbyModule;
import com.mineplex.studio.sdk.modules.world.MineplexWorld;
import com.mineplex.studio.sdk.modules.world.MineplexWorldModule;
import com.mineplex.studio.sdk.modules.world.config.MineplexWorldConfig;
import com.mineplex.studio.sdk.modules.world.config.WorldCreationConfig;
import com.mineplex.studio.scaffold.OneBlockWars;
import com.mineplex.studio.scaffold.StartCommand;
import com.mineplex.studio.scaffold.CommandUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class OneBlockWarsPlugin extends JavaPlugin {
    @Getter
    private static OneBlockWars game;
    private LobbyModule lobbyModule;
    private MineplexGameModule gameModule;

    @Override
    public void onEnable() {
        MineplexWorldModule worldModule = MineplexModuleManager.getRegisteredModule(
                MineplexWorldModule.class);

        MineplexWorld lobby =
                worldModule.createMineplexWorld(
                        MineplexWorldConfig.builder().worldCreationConfig(
                                WorldCreationConfig.builder().worldTemplate(
                                        "lobby").build()).build(),
                        null);

        lobbyModule = MineplexModuleManager.getRegisteredModule(LobbyModule.class);
        lobbyModule.setActiveLobby(lobbyModule.createBasicLobby(lobby));
        lobbyModule.setup();

        gameModule = MineplexModuleManager.getRegisteredModule(MineplexGameModule.class);
        gameModule.setGameCycle(new GameCycle() {
            @Override
            public MineplexGame createNextGame() {
                game = new OneBlockWars(OneBlockWarsPlugin.this);
                return game;
            }

            @Override
            public boolean hasNextGame() {
                return true;
            }
        });

        gameModule.startNextGame();

        CommandUtil.register(new StartCommand(gameModule));
    }

    @Override
    public void onDisable() {
        gameModule.teardown();
        lobbyModule.teardown();
    }
}