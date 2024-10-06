package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.BuiltInPlayerState;
import com.mineplex.studio.scaffold.OneBlockWarsPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class StartedPlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OneBlockWarsPlugin.getGame().setPlayerState(event.getPlayer(), BuiltInPlayerState.ALIVE);
        event.getPlayer().teleport(OneBlockWarsPlugin.getGame().getGameWorld().getMinecraftWorld().getSpawnLocation());
    }
}