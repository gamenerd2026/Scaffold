package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.BuiltInPlayerState;
import com.mineplex.studio.sdk.modules.game.PlayerState;
import com.mineplex.studio.scaffold.OneBlockWars;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {
    private final OneBlockWars game;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerState playerState = game.getPlayerState(player);

        if (playerState.isGameParticipant()) {
            game.setPlayerState(player, BuiltInPlayerState.ELIMINATED);
        }

        game.removePlayerState(event.getPlayer());
    }
}