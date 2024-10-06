package com.mineplex.studio.scaffold;

import com.mineplex.studio.sdk.modules.game.BuiltInGameState;
import com.mineplex.studio.sdk.modules.game.event.PlayerStateChangeEvent;
import com.mineplex.studio.scaffold.OneBlockWars;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class PlayerStateChangeListener implements Listener {
    private final OneBlockWars game;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerStateChange(PlayerStateChangeEvent event) {
        if (!event.getToState().isAlive() && game.getAlivePlayers().size() <= 1) {
            game.setGameState(BuiltInGameState.ENDED);
        }
    }
}