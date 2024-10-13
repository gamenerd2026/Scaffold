package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.BuiltInPlayerState;
import com.mineplex.studio.scaffold.OneBlockWars;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public class PlayerDeathListener implements Listener {
    private final OneBlockWars game;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!game.getPlayerState(event.getPlayer()).isAlive()) {
            return;
        }

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(game.getPlugin(),
                () -> game.setPlayerState(event.getPlayer(), BuiltInPlayerState.ELIMINATED));

    }
}